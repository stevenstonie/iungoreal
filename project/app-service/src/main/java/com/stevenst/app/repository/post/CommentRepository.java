package com.stevenst.app.repository.post;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.stevenst.app.model.Comment;

import jakarta.transaction.Transactional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
	@Transactional
	@Modifying
	void deleteAllByPostId(Long postId);

	@Query("SELECT comment FROM Comment comment WHERE comment.post.id = :postId AND (:cursor IS NULL OR comment.id < :cursor) ORDER BY comment.createdAt DESC")
	List<Comment> findCommentsBeforeCursor(Long postId, Long cursor, Pageable pageable);

	Long countByPostId(Long postId);
}
