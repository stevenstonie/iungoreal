package com.stevenst.app.repository.chat;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.stevenst.lib.model.chat.ChatMessage;

import jakarta.transaction.Transactional;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
	@Query("SELECT msg FROM ChatMessage msg WHERE msg.chatroomId = :chatroomId AND (:cursor IS NULL OR msg.id < :cursor) ORDER BY msg.id DESC")
	List<ChatMessage> findMessagesBeforeCursorByChatroomId(@Param("chatroomId") Long chatroomId,
			@Param("cursor") Long cursor, Pageable pageable);

	@Modifying
	@Transactional
	@Query("DELETE FROM ChatMessage message WHERE message.chatroomId = :chatroomId")
	void deleteAllByChatroomId(@Param("chatroomId") Long chatroomId);
}
