package com.stevenst.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import com.stevenst.app.model.FriendRequests;
import com.stevenst.lib.model.User;

import jakarta.transaction.Transactional;

@Repository
public interface FriendRequestsRepository extends JpaRepository<FriendRequests, Long> {
	boolean existsBySenderAndReceiver(User sender, User receiver);

	@Modifying
	@Transactional
	void deleteBySenderAndReceiver(User sender, User receiver);
}
