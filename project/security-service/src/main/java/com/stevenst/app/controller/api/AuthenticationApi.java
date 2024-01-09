package com.stevenst.app.controller.api;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

import com.stevenst.app.payload.AuthRequest;
import com.stevenst.app.payload.AuthResponse;
import com.stevenst.app.payload.RegisterRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

public interface AuthenticationApi {
	@Operation(summary = "Register", description = "Create a new user account and return JWT token", tags = "Authentication")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Registration Successful", content = {
					@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = String.class)) }),
			@ApiResponse(responseCode = "400", description = "Unauthorized - invalid credentials", content = @Content),
			@ApiResponse(responseCode = "401", description = "Unauthorized - token expired", content = @Content) })

	public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request);

	@Operation(summary = "Login", description = "Authenticate user and return JWT token", tags = "Authentication")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Login Successful", content = {
					@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = String.class)) }),
			@ApiResponse(responseCode = "401", description = "Unauthorized - invalid credentials", content = @Content),
			@ApiResponse(responseCode = "401", description = "Unauthorized - token expired", content = @Content) })

	public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request);
}