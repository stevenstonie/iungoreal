package com.stevenst.app.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.stevenst.lib.exception.IgorIoException;
import com.stevenst.app.exception.IgorPostException;
import com.stevenst.lib.exception.IgorUserNotFoundException;
import com.stevenst.app.model.Post;
import com.stevenst.app.repository.PostRepository;
import com.stevenst.app.repository.UserRepository;
import com.stevenst.app.service.PostService;
import com.stevenst.lib.model.User;
import com.stevenst.lib.payload.ResponsePayload;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
	private final PostRepository postRepository;
	private final UserRepository userRepository;
	@Value("${app.media-path}")
	private String mediaPath;

	@Override
	public ResponseEntity<ResponsePayload> createPost(String title, String description, String authorUsername,
			MultipartFile file) {
		User author = userRepository.findByUsername(authorUsername)
				.orElseThrow(() -> new IgorUserNotFoundException("Author not found"));
		if(title == null || title.isEmpty()) {
			throw new IgorPostException("Title cannot be null or empty");
		}

		String mediaName = "";
		if (file != null) {
			mediaName = storeFileAndReturnFileName(file, authorUsername);
		}

		Post post = Post.builder()
				.author(author)
				.title(title)
				.description(description)
				.mediaName(mediaName)
				.build();
		postRepository.save(post);

		return ResponseEntity.ok(ResponsePayload.builder().status(200).message("Post created successfully").build());
	}

	// ---------------------------------------------

	private String storeFileAndReturnFileName(MultipartFile file, String authorUsername) {
		try {
			String originalFileName = file.getOriginalFilename();
			if (originalFileName == null) {
				throw new IgorPostException("Cannot store a file with a null name.");
			}

			Path targetDirectory = Paths.get(mediaPath, authorUsername, "post_images");
			Files.createDirectories(targetDirectory);
			
			Path targetPath = targetDirectory.resolve(originalFileName);
			if (Files.exists(targetPath)) {
				targetPath = getPathWithUniqueFileName(targetDirectory, originalFileName);
			}

			Files.write(targetPath, file.getBytes(), StandardOpenOption.CREATE_NEW);

			return targetPath.getFileName().toString();
		} catch (IOException e) {
			throw new IgorIoException("Could not store file");
		}
	}

	private Path getPathWithUniqueFileName(Path targetDirectory, String originalFileName) {
		String fileNameWithoutExtension = originalFileName;
		String fileExtension = "";
		int lastIndexOfDot = originalFileName.lastIndexOf('.');
		if (lastIndexOfDot > 0) {
			fileNameWithoutExtension = originalFileName.substring(0, lastIndexOfDot);
			fileExtension = originalFileName.substring(lastIndexOfDot);
		}

		int fileIndex = 0;
		Path filePathWithIndex;
		do {
			fileIndex++;
			String newFileName = fileNameWithoutExtension + "_" + fileIndex + fileExtension;
			filePathWithIndex = targetDirectory.resolve(newFileName);
		} while (Files.exists(filePathWithIndex));

		return filePathWithIndex;
	}
}
