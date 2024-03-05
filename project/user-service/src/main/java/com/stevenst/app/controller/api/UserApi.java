package com.stevenst.app.controller.api;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.stevenst.app.payload.UserPrivatePayload;
import com.stevenst.app.payload.UserPublicPayload;
import com.stevenst.lib.payload.ResponsePayload;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

public interface UserApi {
	@Operation(summary = "Get User Public Payload by Username", description = "Retrieve an user's public data by username", tags = "User")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successful", content = {
					@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserPublicPayload.class)) }),
			@ApiResponse(responseCode = "404", description = "User not found", content = @Content) })

	public ResponseEntity<UserPublicPayload> getUserPublicByUsername(String username);

	@Operation(summary = "Get User Private Payload by Username", description = "Retrieve an user's private data by username", tags = "User")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successful", content = {
					@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserPrivatePayload.class)) }),
			@ApiResponse(responseCode = "404", description = "User not found", content = @Content) })

	public ResponseEntity<UserPrivatePayload> getUserPrivateByUsername(String username);

	@Operation(summary = "Get User by Email", description = "Retrieve an user by email", tags = "User")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successful", content = {
					@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = UserPrivatePayload.class)) }),
			@ApiResponse(responseCode = "404", description = "User not found", content = @Content) })

	public ResponseEntity<UserPrivatePayload> getUserByEmail(String email);

	@Operation(summary = "Save Profile Picture", description = "Save an user's profile picture", tags = "User")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successful", content = {
					@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResponsePayload.class)) }),
			@ApiResponse(responseCode = "404", description = "User not found", content = @Content) })
	public ResponseEntity<ResponsePayload> saveProfilePicture(String username, MultipartFile file);

	@Operation(summary = "Get Profile Picture", description = "Get an user's profile picture", tags = "User")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successful", content = {
					@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = String.class)) }),
			@ApiResponse(responseCode = "404", description = "User not found", content = @Content) })
	public ResponseEntity<String> getProfilePictureLink(String username);

	@Operation(summary = "Delete Profile Picture", description = "Delete an user's profile picture", tags = "User")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successful", content = {
					@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResponsePayload.class)) }),
			@ApiResponse(responseCode = "404", description = "User not found", content = @Content) })
	public ResponseEntity<ResponsePayload> removePfpFromDbAndCloud(String username);
}
