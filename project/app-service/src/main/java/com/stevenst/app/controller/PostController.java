package com.stevenst.app.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.stevenst.app.controller.api.PostApi;
import com.stevenst.app.payload.PostPayload;
import com.stevenst.app.service.PostService;
import com.stevenst.lib.payload.ResponsePayload;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController implements PostApi {
	private final PostService postService;

	@PostMapping("/create")
	public ResponseEntity<ResponsePayload> createPost(
			@RequestParam("authorUsername") String authorUsername,
			@RequestParam("title") String title,
			@RequestParam(value = "description", required = false) String description,
			@RequestParam(value = "files", required = false) List<MultipartFile> file) {
		return ResponseEntity.ok(postService.createPost(authorUsername, title, description, file));
	}

	@GetMapping("/getNextPostsOfUser")
	public ResponseEntity<List<PostPayload>> getAllPosts(@RequestParam("authorUsername") String authorUsername,
			@RequestParam(required = false) Long cursor,
			@RequestParam(defaultValue = "6") int limit) {
		return ResponseEntity.ok(postService.getAllPostsOfUser(authorUsername, cursor, limit));
	}

	@GetMapping("/getNextPostsOfFriends")
	public ResponseEntity<List<PostPayload>> getNextPostsOfFriends(@RequestParam("username") String username,
			@RequestParam(required = false) Long cursor,
			@RequestParam(defaultValue = "3") int limit) {
		return ResponseEntity.ok(postService.getPostsOfFriendsBeforeCursor(username, cursor, limit));
	}
}
