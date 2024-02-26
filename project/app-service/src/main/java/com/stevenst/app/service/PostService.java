package com.stevenst.app.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.stevenst.app.payload.PostPayload;
import com.stevenst.lib.payload.ResponsePayload;

public interface PostService {
	ResponsePayload createPost(String title, String description, String authorUsername, List<MultipartFile> files);

	List<PostPayload> getAllPosts(String authorUsername);

	List<PostPayload> getAllPostsOfAllFriends(String username);
}
