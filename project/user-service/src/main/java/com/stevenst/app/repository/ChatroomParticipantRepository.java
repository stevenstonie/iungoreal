package com.stevenst.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.stevenst.lib.model.User;
import com.stevenst.lib.model.chat.Chatroom;
import com.stevenst.lib.model.chat.ChatroomParticipant;

import jakarta.transaction.Transactional;

@Repository
public interface ChatroomParticipantRepository extends JpaRepository<ChatroomParticipant, Long> {
	@Query("SELECT part FROM ChatroomParticipant part WHERE part.chatroom = :chatroom AND part.user = :user")
	ChatroomParticipant findChatroomParticipantByChatroomAndUser(Chatroom chatroom, User user);

	@Modifying
	@Transactional
	void deleteByChatroomAndUser(Chatroom chatroom, User user);
}
