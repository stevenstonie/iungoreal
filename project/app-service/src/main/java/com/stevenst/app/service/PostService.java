package com.stevenst.app.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.stevenst.app.payload.PostPayload;
import com.stevenst.lib.payload.ResponsePayload;

public interface PostService {
	ResponsePayload createPost(String title, String description, String authorUsername, List<MultipartFile> files);

	List<PostPayload> getNextPostsBeforeCursor(String authorUsername, String username, boolean includeFriends, Long cursor, int limit);

	ResponsePayload upvotePost(String username, Long postId);

	ResponsePayload downvotePost(String username, Long postId);

	ResponsePayload removePost(String username, Long postId);
}
