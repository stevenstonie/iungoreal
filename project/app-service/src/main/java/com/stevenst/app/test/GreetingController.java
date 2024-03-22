package com.stevenst.app.test;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GreetingController {
	@MessageMapping("/hello")
	@SendTo("/topic/greetings")
	public String greeting(String message) throws Exception {
		return "Hello, " + message + "!";
	}
}
