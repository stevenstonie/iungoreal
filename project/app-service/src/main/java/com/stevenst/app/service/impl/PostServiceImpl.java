package com.stevenst.app.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;

import com.stevenst.app.repository.PostRepository;
import com.stevenst.app.service.PostService;
import com.stevenst.lib.payload.ResponsePayload;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
	private final PostRepository postRepository;
	@Value("${app.media-path}")
	private String mediaPath;

	@Override
	public ResponseEntity<ResponsePayload> createPost(String title, String description, String authorUsername,
			MultipartFile file) {
		// search for the author
		
		// create the post

		if (file != null) {
			storeFile(file, authorUsername);
		}

		return ResponseEntity.ok(ResponsePayload.builder().status(200).message("Post created successfully").build());
	}

	// ---------------------------------------------

	private void storeFile(MultipartFile file, String authorUsername) {
		try {
			String originalFileName = file.getOriginalFilename();
			if (originalFileName == null) {
				throw new IllegalArgumentException("Cannot store a file with a null name.");
			}
			
			Path targetDirectory = Paths.get(mediaPath, authorUsername, "post_images");
			Path targetPath = targetDirectory.resolve(originalFileName);
	
			Files.createDirectories(targetDirectory);
	
			if (Files.exists(targetPath)) {
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

				targetPath = filePathWithIndex;
			}
	
			Files.write(targetPath, file.getBytes(), StandardOpenOption.CREATE_NEW);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (MaxUploadSizeExceededException e) {
			e.printStackTrace();
		}	// TODO: you know what to do
	}
}
