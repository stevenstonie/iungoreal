package com.stevenst.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stevenst.app.model.Post;
import com.stevenst.app.model.PostInteraction;
import com.stevenst.lib.model.User;

public interface PostInteractionRepository extends JpaRepository<PostInteraction, Long> {
	PostInteraction findByPostAndUser(Post post, User user);
}
