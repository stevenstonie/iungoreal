package com.stevenst.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stevenst.lib.model.User;
import com.stevenst.lib.model.chat.Chatroom;
import com.stevenst.lib.model.chat.ChatroomParticipant;

@Repository
public interface ChatroomParticipantRepository extends JpaRepository<ChatroomParticipant, Long> {
	void deleteByChatroomAndUser(Chatroom chatroom, User user);
}
