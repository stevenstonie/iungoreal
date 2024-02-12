package com.stevenst.app.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;


@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class DummyController {
	// private final _ _;

	@GetMapping("/hello")
	public String hello() {
		return "hello";
	}
}
