package com.stevenst.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stevenst.app.controller.api.UserApi;
import com.stevenst.lib.model.User;
import com.stevenst.app.service.UserService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController implements UserApi {
	private final UserService userService;

	@GetMapping("/getUserByToken")
	public ResponseEntity<User> getUserByToken(@RequestHeader("Authorization") String authHeader) {
		return ResponseEntity.ok(userService.getUserByToken(authHeader));
	}
}
