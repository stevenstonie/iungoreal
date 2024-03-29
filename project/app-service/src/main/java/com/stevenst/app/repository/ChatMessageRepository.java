package com.stevenst.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stevenst.app.model.ChatMessage;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
	
}
