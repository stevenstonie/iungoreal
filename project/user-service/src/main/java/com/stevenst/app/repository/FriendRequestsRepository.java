package com.stevenst.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stevenst.app.model.FriendRequests;

@Repository
public interface FriendRequestsRepository extends JpaRepository<FriendRequests, Long> {

}
