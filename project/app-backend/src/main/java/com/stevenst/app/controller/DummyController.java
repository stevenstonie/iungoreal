package com.stevenst.app.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/test")
public class DummyController {
	
	@GetMapping("/hello")
	public String hello() {
		return "hello";
	}
}
