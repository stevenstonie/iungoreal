package com.stevenst.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stevenst.lib.model.chat.Chatroom;

@Repository
public interface ChatroomRepository extends JpaRepository<Chatroom, Long> {
	
}
