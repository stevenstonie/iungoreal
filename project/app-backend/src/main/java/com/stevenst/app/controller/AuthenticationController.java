package com.stevenst.app.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stevenst.app.auth.AuthRequest;
import com.stevenst.app.auth.AuthResponse;
import com.stevenst.app.auth.RegisterRequest;
import com.stevenst.app.service.impl.AuthenticationServiceImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController implements AuthenticationApi {
	private final AuthenticationServiceImpl authService;

	@PostMapping("/register")
	public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
		// log("user tries to register");

		AuthResponse authResponse = authService.register(request);

		// if (authResponse.getError() != null) {
		// 	return ResponseEntity.status(401).body(authResponse);
		// }
		// either this^ or another log. depends on if the error is thrown

		return ResponseEntity.ok(authResponse);
	}

	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
		// log("user tries to login");

		AuthResponse authResponse = authService.login(request);

		// if (authResponse.getError() != null) {
		// 	return ResponseEntity.status(401).body(authResponse);
		// }
		// either this^ or another log. depends on if the error is thrown

		return ResponseEntity.ok(authResponse);
	}
}
