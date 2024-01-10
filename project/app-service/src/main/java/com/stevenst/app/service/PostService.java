package com.stevenst.app.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.stevenst.lib.payload.ResponsePayload;

public interface PostService {
	ResponseEntity<ResponsePayload> createPost(String title, String description, String authorUsername, MultipartFile file);
}
