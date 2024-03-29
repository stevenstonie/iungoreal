package com.stevenst.app.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

import com.stevenst.app.model.ChatMessage;

@RestController
public class ChattingController {
	@MessageMapping("/chat.sendToChatroom/{chatroomId}")
	@SendTo("/topic/chatroom/{chatroomId}")
	public ChatMessage greeting(@Payload ChatMessage chatMessage, @DestinationVariable("chatroomId") Long chatroomId) {
		return chatMessage;
	}
}