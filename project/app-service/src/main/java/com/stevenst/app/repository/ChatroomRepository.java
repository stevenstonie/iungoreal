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
	@Query("SELECT participant FROM ChatroomParticipant participant " +
			"INNER JOIN participant.chatroom c " +
			"WHERE c.type = :type AND participant.user = :user")
	List<ChatroomParticipant> findAllByChatroomTypeAndUser(@Param("type") ChatroomType type, @Param("user") User user);
}
