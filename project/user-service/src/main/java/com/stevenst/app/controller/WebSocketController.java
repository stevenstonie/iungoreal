package com.stevenst.app.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebSocketController {
	@MessageMapping("/notification")
	@SendTo("/topic/notification")
	public String handleChatMessage(String message) {
		System.out.println("Received message: " + message);
		return "Hello, " + message;
	}
}
