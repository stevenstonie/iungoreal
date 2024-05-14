package com.stevenst.app.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.stevenst.app.payload.CommentDetachedPayload;
import com.stevenst.app.payload.CommentPayload;
import com.stevenst.app.payload.PostPayload;
import com.stevenst.lib.payload.ResponsePayload;

public interface PostService {
	ResponsePayload createPost(String title, String description, String authorUsername, List<MultipartFile> files);

	CommentPayload addComment(String username, String content, Long postId);

	ResponsePayload setSeen(String username, Long postId);

	List<PostPayload> getNextPostsBeforeCursor(String authorUsername, String username, boolean includeFriends,
			Long cursor, int limit);

	List<CommentPayload> getNextCommentsOfPostBeforeCursor(Long postId, Long cursor, int limit);

	List<CommentDetachedPayload> getNextCommentsOfUserBeforeCursor(String username, Long cursor, int limit);

	List<PostPayload> getNextUpvotedOfUserBeforeCursor(String username, Long cursor, int limit);

	List<PostPayload> getNextDownvotedOfUserBeforeCursor(String username, Long cursor, int limit);

	ResponsePayload upvotePost(String username, Long postId);

	ResponsePayload downvotePost(String username, Long postId);

	ResponsePayload savePost(String username, Long postId);

	ResponsePayload removePost(String username, Long postId);

	ResponsePayload removeComment(String username, Long commentId);
}
