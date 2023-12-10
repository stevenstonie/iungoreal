package com.stevenst.app.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class DummyController {
	
	@GetMapping("/hello")
	public String hello() {
		return "hello";
	}
}
