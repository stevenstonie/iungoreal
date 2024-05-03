package com.stevenst.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.stevenst.app.model.Post;
import com.stevenst.app.model.PostInteraction;

import jakarta.transaction.Transactional;

public interface PostInteractionRepository extends JpaRepository<PostInteraction, Long> {
	PostInteraction findByPostIdAndUserId(Long postId, Long userId);

	@Query("SELECT COUNT(inter) FROM PostInteraction inter WHERE inter.post.id = :postId AND inter.upvoted = true")
	Long countByPostIdAndUpvotedIsTrue(Long postId);

	@Query("SELECT COUNT(inter) FROM PostInteraction inter WHERE inter.post.id = :postId AND inter.downvoted = true")
	Long countByPostIdAndDownvotedIsTrue(Long postId);

	@Transactional
	@Modifying
	void deleteAllByPost(Post post);
}
