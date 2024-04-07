package com.stevenst.app.repository.chat;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.stevenst.lib.model.chat.Chatroom;
import com.stevenst.lib.model.chat.ChatroomParticipant;
import com.stevenst.lib.model.chat.ChatroomType;
import com.stevenst.lib.model.User;

@Repository
public interface ChatroomRepository extends JpaRepository<Chatroom, Long> {
	@Query("SELECT distinct part FROM ChatroomParticipant part " +
			"WHERE part.chatroom.type IN :types AND part.chatroom IN " +
			"(SELECT c FROM ChatroomParticipant p " +
			"JOIN p.chatroom c " +
			"WHERE p.user = :user) " +
			"AND part.user <> :user AND part.hasLeft = false")
	List<ChatroomParticipant> findParticipantsWithCommonChatrooms(@Param("user") User user,
			@Param("types") List<ChatroomType> types);

	@Query("SELECT participant.chatroom FROM ChatroomParticipant participant " +
			"WHERE participant.user = :user AND participant.hasLeft = false")
	List<Chatroom> findChatroomsOfUserNotLeft(User user);
}
