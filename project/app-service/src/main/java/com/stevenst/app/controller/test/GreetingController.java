package com.stevenst.app.controller.test;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

import com.stevenst.app.model.test.Greeting;
import com.stevenst.app.model.test.Message;

@RestController
public class GreetingController {
	@MessageMapping("/hello")
	@SendTo("/topic/greetings")
	public Greeting greeting(Message message) throws Exception {
		return new Greeting("Hello, " + HtmlUtils.htmlEscape(message.name()) + "!");
	}
}
