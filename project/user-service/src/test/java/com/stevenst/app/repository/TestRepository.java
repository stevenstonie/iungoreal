package com.stevenst.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.stevenst.lib.model.User;

import jakarta.transaction.Transactional;

@Repository
public interface TestRepository extends JpaRepository<User, Long> {
	@Transactional
	@Modifying
	@Query("DELETE FROM Friendships")
	void deleteAllFriendships();

	@Transactional
	@Modifying
	@Query("DELETE FROM FriendRequests")
	void deleteAllFriendRequests();

	@Transactional
	@Modifying
	@Query("DELETE FROM User")
	void deleteAllUsers();
}
