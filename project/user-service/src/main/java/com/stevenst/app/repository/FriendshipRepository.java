package com.stevenst.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.stevenst.app.model.Friendship;
import com.stevenst.lib.model.User;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    @Query("SELECT COUNT(f) > 0 FROM Friendship f WHERE " +
           "(f.user1 = :user1 AND f.user2 = :user2) OR " +
           "(f.user1 = :user2 AND f.user2 = :user1)")
    boolean existsByUsers(@Param("user1") User user1, @Param("user2") User user2);
}
