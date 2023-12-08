package com.stevenst.app.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.springframework.web.bind.annotation.GetMapping;


@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/test")
public class DummyController {
	
	@GetMapping("/hello")
	public String hello() {
		return "hello";
	}
}
