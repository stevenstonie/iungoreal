package com.stevenst.app.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
public class HomeController {
	@GetMapping("/")
	public String home() {
		return "hola, home controller";
	}

	@GetMapping("/secured")
	public String secured() {
		return "hola, secured controller";
	}
	
}
