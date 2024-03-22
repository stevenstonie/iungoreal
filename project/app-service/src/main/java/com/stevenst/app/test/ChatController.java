package com.stevenst.app.test;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {
	@MessageMapping("/hello")
	@SendTo("/topic/greetings")
	public ChatMessage greeting(ChatMessage chatMessage) {
		System.out.println("got this message: " + chatMessage);
		return chatMessage;
	}
}