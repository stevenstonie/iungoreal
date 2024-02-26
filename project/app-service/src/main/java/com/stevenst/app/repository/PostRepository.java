package com.stevenst.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stevenst.app.model.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
	List<Post> findAllByAuthorUsernameOrderByCreatedAtDesc(String authorUsername);
}
