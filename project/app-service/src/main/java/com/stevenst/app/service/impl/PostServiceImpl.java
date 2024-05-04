package com.stevenst.app.service.impl;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
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
import com.stevenst.app.model.Comment;
import com.stevenst.app.model.Post;
import com.stevenst.app.model.PostInteraction;
import com.stevenst.app.model.PostMedia;
import com.stevenst.app.payload.PostPayload;
import com.stevenst.app.repository.UserRepository;
import com.stevenst.app.repository.post.CommentRepository;
import com.stevenst.app.repository.post.PostInteractionRepository;
import com.stevenst.app.repository.post.PostMediaRepository;
import com.stevenst.app.repository.post.PostRepository;
import com.stevenst.app.service.PostService;
import com.stevenst.lib.model.User;
import com.stevenst.lib.payload.ResponsePayload;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
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
	private final UserRepository userRepository;
	private final PostRepository postRepository;
	private final PostMediaRepository postMediaRepository;
	private final PostInteractionRepository postInteractionRepository;
	private final CommentRepository commentRepository;
	private final S3Client s3Client;
	private final WebClient webClient;
	@Value("${aws.bucketName}")
	private String bucketName;
	private static final String USERS_PATH = "users/";

	@Override
	public ResponsePayload createPost(String authorUsername, String title, String description,
			List<MultipartFile> files) {
		User author = findUserByUsername(authorUsername);
		if (title == null || title.isEmpty()) {
			throw new IgorPostException("Title cannot be null or empty");
		}

		Post post = savePostInDbAndReturn(author, title, description);

		if (files != null && !files.isEmpty()) {
			List<String> uniqueFilenames = getUniqueFilenamesFromFiles(files);

			saveMediaNamesInDb(post, uniqueFilenames);

			saveMediaFilesInCloud(author.getUsername(), post.getId(), files, uniqueFilenames);
		}

		return ResponsePayload.builder().status(200)
				.message("Post created successfully for " + author.getUsername() + ".").build();
	}

	@Override
	public ResponsePayload addComment(String username, String content, Long postId) {
		User user = findUserByUsername(username);
		Post post = findPostById(postId);

		// create the comment and save it
		commentRepository.save(Comment.builder()
				.author(user)
				.post(post)
				.content(content)
				.build());

		// check if an interaction exists and if not then create it
		PostInteraction postInteraction = postInteractionRepository.findByPostIdAndUserId(post.getId(), user.getId());
		if (postInteraction == null) {
			postInteraction = PostInteraction.builder().user(user).post(post).seen(true).build();
			postInteractionRepository.save(postInteraction);
		}

		return ResponsePayload.builder().status(201).message("Comment added successfully.").build();
	}

	@Override
	public List<PostPayload> getNextPostsBeforeCursor(
			String authorUsername, String username, boolean includeFriends, Long cursor, int limit) {
		User user = findUserByUsername(username);
		List<Post> posts = new ArrayList<>();
		PageRequest pageRequest = PageRequest.of(0, limit, Sort.by("createdAt").descending());

		// get all friends of the user (including the user) or just the user if the posts are from someones profile
		// then get the next 'limit' posts before the cursor
		if (includeFriends) {
			List<String> friendUsernames = getAllFriendsUsernamesOfUser(user.getUsername());
			friendUsernames.add(user.getUsername());

			posts = postRepository.findPostsOfFriendsBeforeCursor(user.getUsername(), friendUsernames,
					cursor, pageRequest);
		} else {
			posts = postRepository.findPostsOfUserBeforeCursor(authorUsername, cursor, pageRequest);
		}

		List<PostPayload> postPayloads = new ArrayList<>();

		// populate the post payloads with data: default, media, interactions, etc
		for (Post post : posts) {
			List<String> mediaNames = postMediaRepository.findMediaNamesByPostId(post.getId());
			// get the interactions of the user to the author's posts
			PostInteraction postInteraction = postInteractionRepository.findByPostIdAndUserId(post.getId(),
					user.getId());
			if (postInteraction == null) {
				postInteraction = new PostInteraction();
			}

			postPayloads.add(PostPayload.builder()
					.id(post.getId())
					.authorUsername(post.getAuthor().getUsername())
					.title(post.getTitle())
					.description(post.getDescription())
					.createdAt(post.getCreatedAt())
					.mediaLinks(getLinksForAllMediaOfAPost(post.getAuthor().getUsername(), post.getId(), mediaNames))
					.upvoteScore(postInteractionRepository.countByPostIdAndUpvotedIsTrue(post.getId()) -
							postInteractionRepository.countByPostIdAndDownvotedIsTrue(post.getId()))
					.nbOfComments(0L)
					.upvoted(postInteraction.isUpvoted())
					.downvoted(postInteraction.isDownvoted())
					.saved(postInteraction.isSaved())
					.seen(postInteraction.isSeen())
					.build());
		}

		// return these posts
		return postPayloads;
	}

	@Override
	public List<Comment> getNextCommentsBeforeCursor(Long postId, Long cursor, int limit) {
		Post post = findPostById(postId);
		PageRequest pageRequest = PageRequest.of(0, limit, Sort.by("createdAt").descending());

		return commentRepository.findCommentsBeforeCursor(post.getId(), cursor, pageRequest);
	}

	@Override
	public ResponsePayload upvotePost(String username, Long postId) {
		User user = findUserByUsername(username);
		Post post = findPostById(postId);
		PostInteraction postInteraction = postInteractionRepository.findByPostIdAndUserId(post.getId(), user.getId());

		if (postInteraction == null) {
			postInteraction = PostInteraction.builder().user(user).post(post).upvoted(true).seen(true).build();

			postInteractionRepository.save(postInteraction);
			return ResponsePayload.builder().status(201).message("Post upvoted.").build();
		} else {
			postInteraction.setUpvoted(!postInteraction.isUpvoted());
			postInteraction.setDownvoted(false);

			postInteractionRepository.save(postInteraction);
			if (postInteraction.isUpvoted()) {
				return ResponsePayload.builder().status(200).message("Post upvoted.").build();
			} else {
				return ResponsePayload.builder().status(200).message("Post unupvoted.").build();
			}
		}
	}

	@Override
	public ResponsePayload downvotePost(String username, Long postId) {
		User user = findUserByUsername(username);
		Post post = findPostById(postId);
		PostInteraction postInteraction = postInteractionRepository.findByPostIdAndUserId(post.getId(), user.getId());

		if (postInteraction == null) {
			postInteraction = PostInteraction.builder().user(user).post(post).downvoted(true).seen(true).build();

			postInteractionRepository.save(postInteraction);
			return ResponsePayload.builder().status(201).message("Post downvoted.").build();
		} else {
			postInteraction.setDownvoted(!postInteraction.isDownvoted());
			postInteraction.setUpvoted(false);

			postInteractionRepository.save(postInteraction);
			if (postInteraction.isDownvoted()) {
				return ResponsePayload.builder().status(200).message("Post downvoted.").build();
			} else {
				return ResponsePayload.builder().status(200).message("Post undownvoted.").build();
			}
		}
	}

	@Override
	public ResponsePayload savePost(String username, Long postId) {
		User user = findUserByUsername(username);
		Post post = findPostById(postId);
		PostInteraction postInteraction = postInteractionRepository.findByPostIdAndUserId(post.getId(), user.getId());

		if (postInteraction == null) {
			postInteraction = PostInteraction.builder().user(user).post(post).saved(true).seen(true).build();

			postInteractionRepository.save(postInteraction);
			return ResponsePayload.builder().status(201).message("Post saved.").build();
		} else {
			postInteraction.setSaved(!postInteraction.isSaved());

			postInteractionRepository.save(postInteraction);
			if (postInteraction.isSaved()) {
				return ResponsePayload.builder().status(200).message("Post saved.").build();
			} else {
				return ResponsePayload.builder().status(200).message("Post unsaved.").build();
			}
		}
	}

	@Override
	public ResponsePayload removePost(String username, Long postId) {
		User user = findUserByUsername(username);
		// get the specific post and check if it belongs to the provided author
		Post post = findPostById(postId);
		if (!post.getAuthor().getUsername().equals(user.getUsername())) {
			throw new IgorPostException("Post with id " + postId + " does not belong to " + user.getUsername());
		}

		// remove the next constraints:

		// comments
		removeCommentsOfPost(postId);

		// media
		removeMediaOfPost(postId, user.getUsername());

		// interactions
		postInteractionRepository.deleteAllByPost(post);

		// and then remove the post from the db and cloud
		postRepository.delete(post);

		return ResponsePayload.builder().status(200)
				.message("Post with id: " + postId + " of user: " + user.getUsername() + " removed successfully.")
				.build();
	}

	// ---------------------------------------------

	private void removeCommentsOfPost(Long postId) {
		commentRepository.deleteAllByPostId(postId);
	}

	private void removeMediaOfPost(Long postId, String username) {
		List<String> mediaNames = postMediaRepository.findMediaNamesByPostId(postId);

		removeMediaOfPostFromCloud(username, postId, mediaNames);
		postMediaRepository.deleteAllByPostId(postId);
	}

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

		// for (String mediaName : mediaNames) {
		// 	try {
		// 		mediaLinks.add(getLinkForAMediaOfAPost(username, postId, mediaName));
		// 	} catch (IgorIoException e) {
		// 		System.err.println("Unable to generate a presigned url:" + e.getMessage());
		// 	}
		// }
		// // commented for development

		return mediaLinks;
	}

	private String getLinkForAMediaOfAPost(String username, Long postId, String mediaName) {
		String key = USERS_PATH + username + "/posts/" + postId + "/" + mediaName;
		try {
			checkIfMediaFileExistsInS3(key);
		} catch (IgorImageNotFoundException e) {
			System.err.println("File not found in S3:" + e.getMessage());
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
		String keyPath = USERS_PATH + username + "/posts/" + postId;

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

	// used to be sure that all files have unique names (even though its unlikely the user uploads the same file twice)
	private List<String> getUniqueFilenamesFromFiles(List<MultipartFile> files) {
		// use a map for file names and the number of times each one appears in the list
		Map<String, Byte> filenameCounts = new HashMap<>();
		List<String> uniqueFilenames = new ArrayList<>();

		for (MultipartFile file : files) {
			String originalFilename = file.getOriginalFilename();
			// search in the map if the file name already exists
			Byte count = filenameCounts.getOrDefault(originalFilename, (byte) 0);

			String uniqueFilename;
			// if the file name already exists, add a new number to the end
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

	private void removeMediaOfPostFromCloud(String authorUsername, Long postId, List<String> filenames) {
		String key = USERS_PATH + authorUsername + "/posts/" + postId + "/";

		for (String filename : filenames) {
			String keyToRemove = key + filename;
			s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucketName).key(keyToRemove).build());
		}

		System.out.println("Successfully removed post media (with id: " + postId + ") from cloud.");
	}

	private User findUserByUsername(String username) {
		return userRepository.findByUsername(username).orElseThrow(
				() -> new IgorUserNotFoundException("User with username " + username + " not found."));
	}

	private Post findPostById(Long postId) {
		if (postId == null) {
			throw new IgorPostException("Post id cannot be null.");
		}
		return postRepository.findById(postId)
				.orElseThrow(() -> new IgorPostException("Post with id " + postId + " not found."));
	}
}
