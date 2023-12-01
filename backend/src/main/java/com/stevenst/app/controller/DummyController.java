package com.stevenst.app.controller;

import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/test")
public class DummyController {

	@GetMapping("/getHello")
	public String getHello() {
		return "Hello";
	}
	
}
