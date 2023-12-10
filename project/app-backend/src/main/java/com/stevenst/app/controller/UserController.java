package com.stevenst.app.controller;

import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stevenst.app.model.User;
import com.stevenst.app.service.UserService;
import com.stevenst.app.service.impl.JwtServiceImpl;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/user")
public class UserController {
	private final JwtServiceImpl jwtService;
	private final UserService userService;

	public UserController(JwtServiceImpl jwtService, UserService userService) {
		this.jwtService = jwtService;
		this.userService = userService;
	}

	@GetMapping("/currentUser")
	public Optional<User> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
		String token = jwtService.extractToken(authHeader);
		String email = jwtService.extractUsername(token);
		Optional<User> user = userService.getUserByEmail(email);

		if (!jwtService.isTokenValid(token, email)) {
			return Optional.empty();
		}

		return user;
	}

}
