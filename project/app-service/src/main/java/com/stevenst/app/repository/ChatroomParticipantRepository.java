package com.stevenst.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.stevenst.app.model.Chatroom;
import com.stevenst.app.model.ChatroomParticipant;
import com.stevenst.lib.model.User;

import jakarta.transaction.Transactional;

@Repository
public interface ChatroomParticipantRepository extends JpaRepository<ChatroomParticipant, Long> {
	@Query("SELECT participant FROM ChatroomParticipant participant WHERE participant.chatroom IN :chatrooms")
	List<ChatroomParticipant> findParticipantsInChatrooms(List<Chatroom> chatrooms);

	ChatroomParticipant findByUserAndChatroom(User user, Chatroom chatroom);

	List<ChatroomParticipant> findByChatroom(Chatroom chatroom);

	@Modifying
	@Transactional
	Long deleteByChatroom(Chatroom chatroom);

	Long countByChatroomAndHasLeftIsFalse(Chatroom chatroom);

	@Query("SELECT part.chatroom FROM ChatroomParticipant part " +
			"WHERE part.user = :user " +
			"AND part.hasLeft = false " +
			"AND part.chatroom.type = 'DM'")
	List<Chatroom> findDmChatroomsOfUserNotLeft(User user);

	@Query("SELECT part.chatroom FROM ChatroomParticipant part " +
			"WHERE part.user = :user " +
			"AND part.chatroom.type = 'GROUP'")
	List<Chatroom> findGroupChatroomsOfUser(User user);

	@Query("SELECT part FROM ChatroomParticipant part " +
			"WHERE part.chatroom IN :chatrooms " +
			"AND part.user <> :user")
	List<ChatroomParticipant> findParticipantsInTheseChatroomsExcludingUser(List<Chatroom> chatrooms, User user);

	@Query("SELECT part.chatroom FROM ChatroomParticipant part " +
			"WHERE part.user = :user1 " +
			"AND part.chatroom IN (SELECT part2.chatroom FROM ChatroomParticipant part2 " +
			"WHERE part2.user = :user2 " +
			"AND part2.chatroom.type = 'DM')")
	Chatroom findCommonDmChatroomOfUsers(User user1, User user2);
}
