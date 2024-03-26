package com.stevenst.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.stevenst.app.model.Chatroom;
import com.stevenst.app.model.ChatroomParticipant;
import com.stevenst.app.model.ChatroomType;
import com.stevenst.lib.model.User;

@Repository
public interface ChatroomRepository extends JpaRepository<Chatroom, Long> {
	@Query("SELECT distinct cp FROM ChatroomParticipant cp " +
			"WHERE cp.chatroom.type = :type AND cp.chatroom IN " +
			"(SELECT c FROM ChatroomParticipant p " +
			"JOIN p.chatroom c " +
			"WHERE p.user = :user) " +
			"AND cp.user <> :user")
	List<ChatroomParticipant> findParticipantsWithCommonChatrooms(@Param("type") ChatroomType type,
			@Param("user") User user);
}
