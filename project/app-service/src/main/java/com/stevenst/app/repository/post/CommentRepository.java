package com.stevenst.app.repository.post;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stevenst.app.model.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
	
}
