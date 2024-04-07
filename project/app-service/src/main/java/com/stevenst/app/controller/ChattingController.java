package com.stevenst.app.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

import com.stevenst.lib.model.chat.ChatMessage;
import com.stevenst.app.service.ChatService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ChattingController {
	private final ChatService chatService;

	@MessageMapping("/chat.sendToChatroom/{chatroomId}")
	@SendTo("/topic/chatroom/{chatroomId}")
	public ChatMessage sendMessage(@Payload ChatMessage chatMessage, @DestinationVariable("chatroomId") Long chatroomId) {
		chatService.insertMessageIntoDb(chatMessage);
		// TODO: find out how saving in batch works and maybe implement it
		return chatMessage;
	}
}