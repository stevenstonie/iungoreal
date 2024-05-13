package com.stevenst.app.repository.post;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.stevenst.app.model.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
	@Query("SELECT post FROM Post post WHERE post.author.username = :username AND (:cursor IS NULL OR post.id < :cursor) ORDER BY post.createdAt DESC")
	List<Post> findPostsOfUserBeforeCursor(String username, Long cursor, Pageable pageable);

	@Query("SELECT post FROM Post post "
			+ "LEFT JOIN PostInteraction inter ON post.id = inter.post.id AND inter.user.username = :currentUser "
			+ "WHERE post.author.username IN :friendUsernames AND (:cursor IS NULL OR post.id < :cursor) " // AND (interaction.id IS NULL OR interaction.seen = false) <-- add this when implementing the seen logic
			+ "ORDER BY post.createdAt DESC")
	List<Post> findPostsOfFriendsBeforeCursor(String currentUser, List<String> friendUsernames, Long cursor,
			Pageable pageable);

	@Query("SELECT post FROM Post post "
			+ "JOIN PostInteraction inter ON post.id = inter.post.id "
			+ "WHERE inter.user.username = :username AND inter.upvoted = true AND (:cursor IS NULL OR post.id < :cursor) "
			+ "ORDER BY post.createdAt DESC")
	List<Post> findNextUpvotedPostsByUser(String username, Long cursor, Pageable pageable);
}
