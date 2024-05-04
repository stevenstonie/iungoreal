package com.stevenst.app.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.stevenst.app.controller.api.PostApi;
import com.stevenst.app.model.Comment;
import com.stevenst.app.payload.CommentPayload;
import com.stevenst.app.payload.PostPayload;
import com.stevenst.app.service.PostService;
import com.stevenst.lib.payload.ResponsePayload;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController implements PostApi {
	private final String DEFAULT_LIMIT_OF_POSTS = "5";
	private final PostService postService;

	@PostMapping("/create")
	public ResponseEntity<ResponsePayload> createPost(
			@RequestParam("authorUsername") String authorUsername,
			@RequestParam("title") String title,
			@RequestParam(value = "description", required = false) String description,
			@RequestParam(value = "files", required = false) List<MultipartFile> file) {
		return ResponseEntity.ok(postService.createPost(authorUsername, title, description, file));
	}

	@PostMapping("/addComment")
	public ResponseEntity<CommentPayload> addComment(@RequestParam("username") String username,
			@RequestParam("content") String content, @RequestParam("postId") Long postId) {
		return ResponseEntity.ok(postService.addComment(username, content, postId));
	}

	@GetMapping("/getNextPostsOfAuthor")
	public ResponseEntity<List<PostPayload>> getNextPostsOfAuthor(@RequestParam("authorUsername") String authorUsername,
			@RequestParam("username") String username,
			@RequestParam(required = false) Long cursor,
			@RequestParam(defaultValue = DEFAULT_LIMIT_OF_POSTS) int limit) {
		return ResponseEntity.ok(postService.getNextPostsBeforeCursor(authorUsername, username, false, cursor, limit));
	}

	@GetMapping("/getNextPostsOfFriends")
	public ResponseEntity<List<PostPayload>> getNextPostsOfFriends(@RequestParam("username") String username,
			@RequestParam(required = false) Long cursor,
			@RequestParam(defaultValue = DEFAULT_LIMIT_OF_POSTS) int limit) {
		return ResponseEntity.ok(postService.getNextPostsBeforeCursor(username, username, true, cursor, limit));
	}

	@GetMapping("/getNextCommentsOfPost")
	public ResponseEntity<List<CommentPayload>> getNextCommentsOfPost(@RequestParam("postId") Long postId,
			@RequestParam(required = false) Long cursor,
			@RequestParam(defaultValue = "20") int limit) {
		return ResponseEntity.ok(postService.getNextCommentsBeforeCursor(postId, cursor, limit));
	}

	@PutMapping("/upvote")
	public ResponseEntity<ResponsePayload> upvotePost(@RequestParam("username") String username,
			@RequestParam("postId") Long postId) {
		return ResponseEntity.ok(postService.upvotePost(username, postId));
	}

	@PutMapping("/downvote")
	public ResponseEntity<ResponsePayload> downvotePost(@RequestParam("username") String username,
			@RequestParam("postId") Long postId) {
		return ResponseEntity.ok(postService.downvotePost(username, postId));
	}

	@PutMapping("/save")
	public ResponseEntity<ResponsePayload> savePost(@RequestParam("username") String username,
			@RequestParam("postId") Long postId) {
		return ResponseEntity.ok(postService.savePost(username, postId));
	}

	@DeleteMapping("/remove")
	public ResponseEntity<ResponsePayload> removePost(@RequestParam("username") String username,
			@RequestParam("postId") Long postId) {
		return ResponseEntity.ok(postService.removePost(username, postId));
	}
}
