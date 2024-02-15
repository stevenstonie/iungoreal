package com.stevenst.app.service;

import org.springframework.web.multipart.MultipartFile;

import com.stevenst.lib.payload.ResponsePayload;

public interface PostService {
	ResponsePayload createPost(String title, String description, String authorUsername, MultipartFile file);
}
