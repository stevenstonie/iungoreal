package com.stevenst.app.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

import com.stevenst.app.model.ChatMessage;

@RestController
public class ChattingController {
	@MessageMapping("/chat.sendToChatroom")
	@SendTo("/topic/chatroom")
	public ChatMessage greeting(ChatMessage chatMessage) {
		return chatMessage;
	}
}