package com.stevenst.app.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.stevenst.app.controller.api.PostApi;
import com.stevenst.app.model.Post;
import com.stevenst.app.service.PostService;
import com.stevenst.lib.payload.ResponsePayload;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController implements PostApi {
	private final PostService postService;

	@PostMapping("/create")
	public ResponseEntity<ResponsePayload> createPost(
			@RequestParam("title") String title, @RequestParam("description") String description,
			@RequestParam("authorUsername") String authorUsername, @RequestParam(value = "file", required = false) MultipartFile file) {
		return postService.createPost(title, description, authorUsername, file);
	}
}
