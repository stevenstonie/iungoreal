package com.stevenst.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stevenst.app.model.Friendships;

@Repository
public interface FriendshipsRepository extends JpaRepository<Friendships, Long> {
	
}
