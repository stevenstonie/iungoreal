package com.stevenst.app.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class WebSocketController {
	private final SimpMessagingTemplate template;

	@MessageMapping("/send/message")
	public void sendMessage(String message) {
		System.out.println(message);
		this.template.convertAndSend("/message", message);
	}
}
