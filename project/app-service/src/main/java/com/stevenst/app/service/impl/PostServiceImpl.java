package com.stevenst.app.service.impl;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import com.stevenst.app.exception.IgorPostException;
import com.stevenst.lib.exception.IgorImageNotFoundException;
import com.stevenst.lib.exception.IgorIoException;
import com.stevenst.lib.exception.IgorUserNotFoundException;
import com.stevenst.app.model.Post;
import com.stevenst.app.model.PostMedia;
import com.stevenst.app.payload.PostPayload;
import com.stevenst.app.repository.PostMediaRepository;
import com.stevenst.app.repository.PostRepository;
import com.stevenst.app.repository.UserRepository;
import com.stevenst.app.service.PostService;
import com.stevenst.lib.model.User;
import com.stevenst.lib.payload.ResponsePayload;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {
	private final PostRepository postRepository;
	private final UserRepository userRepository;
	private final PostMediaRepository postMediaRepository;
	private final S3Client s3Client;
	private final WebClient webClient;
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

	@Override
	public List<PostPayload> getAllPostsOfAnUser(String authorUsername) {
		User author = userRepository.findByUsername(authorUsername).orElseThrow(
				() -> new IgorUserNotFoundException("User with username " + authorUsername + " not found."));
		List<Post> posts = postRepository.findAllByAuthorUsernameOrderByCreatedAtDesc(author.getUsername());
		List<PostPayload> postsDetails = new ArrayList<>();

		for (Post post : posts) {
			List<String> mediaNames = postMediaRepository.findMediaNamesByPostId(post.getId());
			List<String> mediaLinks = getLinksForAllMediaOfAPost(author.getUsername(), post.getId(), mediaNames);

			Long likes = 0L;
			Long dislikes = 0L;

			postsDetails.add(PostPayload.builder()
					.id(post.getId())
					.authorUsername(author.getUsername())
					.title(post.getTitle())
					.description(post.getDescription())
					.createdAt(post.getCreatedAt())
					.mediaLinks(mediaLinks)
					.likes(likes)
					.dislikes(dislikes)
					.build());
		}

		return postsDetails;
	}

	@Override
	public List<PostPayload> getPostsOfFriendsBeforeCursorId(String username, Long cursorId, int limit) {
		User user = userRepository.findByUsername(username).orElseThrow(
				() -> new IgorUserNotFoundException("User with username " + username + " not found."));

		PageRequest pageRequest = PageRequest.of(0, limit, Sort.by("createdAt").descending());

		// get all friends of the user (including the user)
		List<String> friendUsernames = getAllFriendsUsernamesOfUser(username);
		friendUsernames.add(username);

		// get the next 'limit' posts before the cursorId for the user
		List<Post> posts = postRepository.findPostsFromFriendsBeforeCursorId(username, friendUsernames, cursorId,
				pageRequest);

		List<PostPayload> postPayloads = new ArrayList<>();

		for (Post post : posts) {
			List<String> mediaNames = postMediaRepository.findMediaNamesByPostId(post.getId());

			postPayloads.add(PostPayload.builder()
					.id(post.getId())
					.authorUsername(post.getAuthor().getUsername())
					.title(post.getTitle())
					.description(post.getDescription())
					.createdAt(post.getCreatedAt())
					.mediaLinks(getLinksForAllMediaOfAPost(username, post.getId(), mediaNames))
					.build());
		}

		// return these posts
		return postPayloads;
	}

	// ---------------------------------------------

	private List<String> getAllFriendsUsernamesOfUser(String username) {
		// get all friends of user
		Mono<List<String>> friendsUsernamesMono = webClient.get()
				.uri(uriBuilder -> uriBuilder.path("/api/friend/getAllFriendsUsernames")
						.queryParam("username", username)
						.build())
				.retrieve()
				.bodyToMono(new ParameterizedTypeReference<List<String>>() {
				});

		// Convert the Mono to a List and return it
		return friendsUsernamesMono.block();
	}

	private List<String> getLinksForAllMediaOfAPost(String username, Long postId, List<String> mediaNames) {
		List<String> mediaLinks = new ArrayList<>();

		for (String mediaName : mediaNames) {
			try {
				mediaLinks.add(getLinkForAMediaOfAPost(username, postId, mediaName));
			} catch (IgorIoException e) {
				System.err.println("Unable to generate a presigned url:" + e.getMessage());
			}
		}

		return mediaLinks;
	}

	private String getLinkForAMediaOfAPost(String username, Long postId, String mediaName) {
		String key = username + "/posts/" + postId + "/" + mediaName;
		try {
			checkIfMediaFileExistsInS3(key);
		} catch (IgorImageNotFoundException e) {
			return null;
		}

		return generatePresignedUrl(key);
	}

	private void checkIfMediaFileExistsInS3(String key) {
		HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
				.bucket(bucketName)
				.key(key)
				.build();

		try {
			s3Client.headObject(headObjectRequest);
		} catch (NoSuchKeyException e) {
			throw new IgorImageNotFoundException(e.getMessage());
		} catch (S3Exception e) {
			throw new IgorIoException(e.getMessage());
		}
	}

	private String generatePresignedUrl(String key) {
		GetObjectRequest getObjectRequest = createGetObjectRequest(key);
		Duration expiration = Duration.ofHours(1);

		try (S3Presigner presigner = S3Presigner.builder().region(Region.EU_CENTRAL_1).build()) {
			GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
					.getObjectRequest(getObjectRequest)
					.signatureDuration(expiration)
					.build();

			PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);

			return presignedRequest.url().toString();
		} catch (S3Exception e) {
			throw new IgorIoException(e.getMessage());
		}
	}

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
			throw new IllegalArgumentException("Change this into a custom exception pointing to a 500 status error"); // TODO:
		}

		for (int i = 0; i < files.size(); i++) {
			MultipartFile file = files.get(i);
			String filename = filenames.get(i);
			String key = keyPath + "/" + filename;

			saveFileInCloud(file, key);
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

	private void saveFileInCloud(MultipartFile file, String key) {
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

	private GetObjectRequest createGetObjectRequest(String key) {

		return GetObjectRequest.builder()
				.bucket(bucketName)
				.key(key)
				.build();
	}
}
