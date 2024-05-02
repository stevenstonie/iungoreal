package com.stevenst.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import com.stevenst.app.model.Post;
import com.stevenst.app.model.PostInteraction;
import com.stevenst.lib.model.User;

import jakarta.transaction.Transactional;

public interface PostInteractionRepository extends JpaRepository<PostInteraction, Long> {
	PostInteraction findByPostAndUser(Post post, User user);

	PostInteraction findByPostIdAndUserId(Long postId, Long userId);

	@Transactional
	@Modifying
	void deleteAllByPost(Post post);
}
