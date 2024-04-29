package com.stevenst.app.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.stevenst.app.model.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
	List<Post> findAllByAuthorUsernameOrderByCreatedAtDesc(String authorUsername);

	@Query("SELECT post FROM Post post WHERE post.author.username IN :friendUsernames AND (:cursor IS NULL OR post.id < :cursor) ORDER BY post.createdAt DESC")
	// TODO: also add the seen = false condition
	List<Post> findPostsFromFriendsBeforeCursorId(List<String> friendUsernames, Long cursor, Pageable pageable);
}
