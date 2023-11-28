package com.stevenst.app.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@RestController
@RequestMapping("/api")
public class HomeController {
	@GetMapping("/home")
	public String home() {
		return "hola, home controller";
	}

	@GetMapping("/secured")
	public String secured() {
		return "hola, secured controller";
	}
	
}
