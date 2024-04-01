package com.stevenst.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.stevenst.app.model.Chatroom;
import com.stevenst.app.model.ChatroomParticipant;
import com.stevenst.app.model.ChatroomType;
import com.stevenst.lib.model.User;

import jakarta.transaction.Transactional;

@Repository
public interface ChatroomParticipantRepository extends JpaRepository<ChatroomParticipant, Long> {
	@Query("SELECT participant.chatroom FROM ChatroomParticipant participant WHERE participant.user = :user AND participant.chatroom.type = :type")
	List<Chatroom> findChatroomsOfUserAndType(User user, ChatroomType type);

	@Query("SELECT participant FROM ChatroomParticipant participant WHERE participant.chatroom IN :chatrooms")
	List<ChatroomParticipant> findParticipantsInChatrooms(List<Chatroom> chatrooms);

	ChatroomParticipant findByUserAndChatroom(User user, Chatroom chatroom);

	@Modifying
	@Transactional
	Long deleteByUserAndChatroom(User user, Chatroom chatroom);

	Long countByChatroom(Chatroom chatroom);
}
