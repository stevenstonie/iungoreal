package com.stevenst.app.controller;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stevenst.app.model.User;
import com.stevenst.app.service.UserService;
import com.stevenst.app.service.impl.JwtServiceImpl;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController implements UserApi {
	private final JwtServiceImpl jwtService;
	private final UserService userService;

	@GetMapping("/currentUser")
	public ResponseEntity<User> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
		String token = jwtService.extractToken(authHeader);
		String email = jwtService.extractUsername(token);
		Optional<User> user = userService.getUserByEmail(email);

		if (!user.isPresent()) {
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok(user.get());
	}
}
