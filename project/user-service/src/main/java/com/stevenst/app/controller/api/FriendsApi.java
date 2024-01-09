package com.stevenst.app.controller.api;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.stevenst.lib.payload.ResponsePayload;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

public interface FriendsApi {
	@Operation(summary = "Send Friend Request", description = "Send a friend request from an user to another an user", tags = "User")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successful", content = {
					@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResponsePayload.class)) }),
			@ApiResponse(responseCode = "404", description = "User not found", content = @Content) })

	public ResponseEntity<ResponsePayload> sendFriendRequest(String sender, String receiver);

	@Operation(summary = "Check Friend Request", description = "Check if an user sent a friend request to another user", tags = "User")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successful or friend request not found", content = {
					@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResponsePayload.class)) }),
			@ApiResponse(responseCode = "404", description = "User not found", content = @Content) })

	public ResponseEntity<ResponsePayload> checkFriendRequest(String from, String to);

	@Operation(summary = "Check Friendship", description = "Check if two users are friends", tags = "User")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successful or friendship not found", content = {
					@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResponsePayload.class)) }),
			@ApiResponse(responseCode = "404", description = "User not found", content = @Content) })

	public ResponseEntity<ResponsePayload> checkFriendship(String user1, String user2);

	@Operation(summary = "Get All Friends", description = "Get all friends of an user", tags = "User")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successful", content = {
					@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(type = "array", implementation = String.class)) }),
			@ApiResponse(responseCode = "404", description = "User not found", content = @Content) })

	public ResponseEntity<List<String>> getAllFriendsUsernames(String username);

	@Operation(summary = "Accept Friend Request", description = "Accept an user's friend request", tags = "User")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successful", content = {
					@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResponsePayload.class)) }),
			@ApiResponse(responseCode = "404", description = "User not found", content = @Content) })

	public ResponseEntity<ResponsePayload> acceptFriendRequest(String sender, String receiver);

	@Operation(summary = "Cancel Friend Request", description = "Cancel an user's friend request to another user", tags = "User")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successful", content = {
					@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResponsePayload.class)) }),
			@ApiResponse(responseCode = "404", description = "User not found", content = @Content),
			@ApiResponse(responseCode = "404", description = "Friend request not found", content = @Content) })

	public ResponseEntity<ResponsePayload> cancelFriendRequest(String sender, String receiver);

	@Operation(summary = "Decline Friend Request", description = "Decline an user's friend request to another user", tags = "User")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successful", content = {
					@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResponsePayload.class)) }),
			@ApiResponse(responseCode = "404", description = "User not found", content = @Content),
			@ApiResponse(responseCode = "404", description = "Friend request not found", content = @Content) })

	public ResponseEntity<ResponsePayload> declineFriendRequest(String sender, String receiver);

	@Operation(summary = "Unfriend", description = "Unfriend an user from another user", tags = "User")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successful", content = {
					@Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = ResponsePayload.class)) }),
			@ApiResponse(responseCode = "404", description = "User not found", content = @Content),
			@ApiResponse(responseCode = "404", description = "Friendship not found", content = @Content) })

	public ResponseEntity<ResponsePayload> unfriend(String unfriender, String unfriended);
}