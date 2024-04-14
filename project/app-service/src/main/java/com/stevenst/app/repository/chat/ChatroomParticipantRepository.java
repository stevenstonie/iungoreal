package com.stevenst.app.repository.chat;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.stevenst.lib.model.chat.Chatroom;
import com.stevenst.lib.model.chat.ChatroomParticipant;
import com.stevenst.lib.model.User;

import jakarta.transaction.Transactional;

@Repository
public interface ChatroomParticipantRepository extends JpaRepository<ChatroomParticipant, Long> {
	@Query("SELECT participant FROM ChatroomParticipant participant WHERE participant.chatroom IN :chatrooms")
	List<ChatroomParticipant> findParticipantsInChatrooms(List<Chatroom> chatrooms);

	ChatroomParticipant findByUserAndChatroom(User user, Chatroom chatroom);

	List<ChatroomParticipant> findByChatroom(Chatroom chatroom);

	@Query("SELECT p FROM ChatroomParticipant p " +
			"WHERE p.chatroom.id = :chatroomId " +
			"ORDER BY p.addedAt ASC")
	List<ChatroomParticipant> findParticipantsInChatroomFromOldest(Long chatroomId, Pageable pageable);

	@Query("SELECT part.chatroom FROM ChatroomParticipant part " +
			"WHERE part.user = :user " +
			"AND part.hasLeft = false " +
			"AND part.chatroom.type = 'DM'")
	List<Chatroom> findDmChatroomsOfUserNotLeft(User user);

	@Query("SELECT part.chatroom FROM ChatroomParticipant part " +
			"WHERE part.user = :user " +
			"AND part.chatroom.type = 'GROUP'")
	List<Chatroom> findGroupChatroomsOfUser(User user);

	@Query("SELECT part.chatroom FROM ChatroomParticipant part " +
			"WHERE part.user = :user " +
			"AND part.chatroom.type = 'REGIONAL'")
	List<Chatroom> findRegionalChatroomsOfUser(User user);

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

	Long countByChatroomAndHasLeftIsFalse(Chatroom chatroom);
	
	@Modifying
	@Transactional
	Long deleteByChatroom(Chatroom chatroom);

	@Modifying
	@Transactional
	@Query("DELETE FROM ChatroomParticipant participant WHERE participant.chatroom = :chatroom")
	void deleteAllByChatroom(Chatroom chatroom);
}
