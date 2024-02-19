package com.stevenst.app.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.stevenst.app.exception.IgorPostException;
import com.stevenst.lib.exception.IgorIoException;
import com.stevenst.lib.exception.IgorUserNotFoundException;
import com.stevenst.app.model.Post;
import com.stevenst.app.model.PostMedia;
import com.stevenst.app.repository.PostMediaRepository;
import com.stevenst.app.repository.PostRepository;
import com.stevenst.app.repository.UserRepository;
import com.stevenst.app.service.PostService;
import com.stevenst.lib.model.User;
import com.stevenst.lib.payload.ResponsePayload;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
	private final PostRepository postRepository;
	private final UserRepository userRepository;
	private final PostMediaRepository postMediaRepository;
	private final S3Client s3Client;
	@Value("${aws.bucketName}")
	private String bucketName;

	@Override
	public ResponsePayload createPost(String authorUsername, String title, String description,
			List<MultipartFile> files) {
		User author = userRepository.findByUsername(authorUsername).orElseThrow(
				() -> new IgorUserNotFoundException("User with username " + authorUsername + " not found."));
		if (title == null || title.isEmpty()) {
			throw new IgorPostException("Title cannot be null or empty");
		}

		Post post = savePostInDbAndReturn(author, title, description);

		if (files.size() > 0) {
			List<String> uniqueFilenames = getUniqueFilenamesFromFiles(files);

			saveMediaNamesInDb(post, uniqueFilenames);

			saveMediaFilesInCloud(author.getUsername(), post.getId(), files, uniqueFilenames);
		}

		return ResponsePayload.builder().status(200)
				.message("Post created successfully for " + author.getUsername() + ".").build();
	}

	// ---------------------------------------------

	private Post savePostInDbAndReturn(User author, String title, String description) {
		Post post = Post.builder()
				.author(author)
				.title(title)
				.description(description)
				.build();
		return postRepository.save(post);
	}

	private void saveMediaNamesInDb(Post post, List<String> filenames) {
		for (int i = 0; i < filenames.size(); i++) {
			PostMedia media = PostMedia.builder()
					.post(post)
					.mediaIndex((byte) i)
					.mediaName(filenames.get(i))
					.build();

			postMediaRepository.save(media);
		}
	}

	private void saveMediaFilesInCloud(String username, Long postId, List<MultipartFile> files,
			List<String> filenames) {
		String keyPath = username + "/posts/" + postId;

		if (files.size() != filenames.size()) {
			throw new IllegalArgumentException("Change this into a custom exception pointing to a 500 status error");	// TODO:
		}

		for (int i = 0; i < files.size(); i++) {
			MultipartFile file = files.get(i);
			String filename = filenames.get(i);
			String key = keyPath + "/" + filename;

			saveFileInCloud(file, filename, key);
		}
	}

	private List<String> getUniqueFilenamesFromFiles(List<MultipartFile> files) {
		Map<String, Byte> filenameCounts = new HashMap<>();
		List<String> uniqueFilenames = new ArrayList<>();

		for (MultipartFile file : files) {
			String originalFilename = file.getOriginalFilename();
			Byte count = filenameCounts.getOrDefault(originalFilename, (byte) 0);

			String uniqueFilename;
			if (count > 0) {
				String baseName = FilenameUtils.removeExtension(originalFilename);
				String extension = FilenameUtils.getExtension(originalFilename);
				uniqueFilename = baseName + "_" + count + (extension.isEmpty() ? "" : "." + extension);
			} else {
				uniqueFilename = originalFilename;
			}

			filenameCounts.put(originalFilename, (byte) (count + 1));
			uniqueFilenames.add(uniqueFilename);
		}

		return uniqueFilenames;
	}

	private void saveFileInCloud(MultipartFile file, String filename, String key) {
		try {
			Map<String, String> metadata = new HashMap<>();
			String contentType = file.getContentType();

			if (contentType != null && !contentType.isEmpty()) {
				metadata.put("Content-Type", contentType);
			}

			RequestBody requestBody = RequestBody.fromInputStream(file.getInputStream(), file.getSize());

			PutObjectRequest putObjectRequest = PutObjectRequest.builder()
					.bucket(bucketName)
					.key(key)
					.metadata(metadata)
					.build();

			s3Client.putObject(putObjectRequest, requestBody);
		} catch (IOException e) {
			throw new IgorIoException(e.getMessage());
		}
	}
}
