package com.stevenst.app.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stevenst.lib.model.chat.ChatMessage;
import com.stevenst.app.payload.ChatroomPayload;
import com.stevenst.app.service.ChatService;
import com.stevenst.lib.payload.ResponsePayload;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {
	private final ChatService chatService;

	@GetMapping("/getFriendsWithNoDmChats")
	public ResponseEntity<List<String>> getFriendsWithoutDmChatrooms(@RequestParam("username") String username) {
		return ResponseEntity.ok(chatService.getFriendsWithoutDmChatrooms(username));
	}

	@GetMapping("/getFriendsNotInChatroom")
	public ResponseEntity<List<String>> getFriendsNotInChatroom(@RequestParam("username") String username,
			@RequestParam("chatroomId") Long chatroomId) {
		return ResponseEntity.ok(chatService.getFriendsNotInChatroom(username, chatroomId));
	}

	@GetMapping("/getAllDmChatroomsOfUser")
	public ResponseEntity<List<ChatroomPayload>> getAllDmChatroomsOfUser(@RequestParam("username") String username) {
		return ResponseEntity.ok(chatService.getAllDmChatroomsOfUser(username));
	}

	@GetMapping("/getAllGroupChatroomsOfUser")
	public ResponseEntity<List<ChatroomPayload>> getAllGroupChatroomsOfUser(@RequestParam("username") String username) {
		return ResponseEntity.ok(chatService.getAllGroupChatroomsOfUser(username));
	}

	@GetMapping("/getAllRegionalChatroomsOfUser")
	public ResponseEntity<List<ChatroomPayload>> getAllRegionalChatroomsOfUser(
			@RequestParam("username") String username) {
		return ResponseEntity.ok(chatService.getAllRegionalChatroomsOfUser(username));
	}

	@GetMapping("/getNextMessagesByChatroomId")
	public ResponseEntity<List<ChatMessage>> getNextMessagesByChatroomId(@RequestParam Long chatroomId,
			@RequestParam(required = false) Long cursor,
			@RequestParam(defaultValue = "50") int limit) {
		return ResponseEntity.ok(chatService.getMessagesBeforeCursorByChatroomId(chatroomId, cursor, limit));
	}

	@GetMapping("/getAllMembersUsernamesInChatroom")
	public ResponseEntity<List<String>> getAllMembersUsernamesInChatroom(@RequestParam("chatroomId") Long chatroomId) {
		return ResponseEntity.ok(chatService.getAllMembersUsernamesInChatroom(chatroomId));
	}

	@PostMapping("/createDmChatroom")
	public ResponseEntity<ChatroomPayload> createDmChatroom(@RequestParam("username") String username,
			@RequestParam("friendUsername") String friendUsername) {
		return ResponseEntity.ok(chatService.createDmChatroom(username, friendUsername));
	}

	@PostMapping("/createGroupChatroom")
	public ResponseEntity<ChatroomPayload> createGroupChatroom(@RequestParam("username") String username) {
		return ResponseEntity.ok(chatService.createGroupChatroom(username));
	}

	@PostMapping("/addUserToGroupChatroom")
	public ResponseEntity<ResponsePayload> addUserToGroupChatroom(@RequestParam("username") String username,
			@RequestParam("chatroomId") Long chatroomId,
			@RequestParam("usernameOfUserToAdd") String usernameOfUserToAdd) {
		return ResponseEntity.ok(chatService.addUserToGroupChatroom(username, chatroomId, usernameOfUserToAdd));
	}

	@PutMapping("/updateChatroomName")
	public ResponseEntity<ResponsePayload> updateChatroomName(@RequestParam("chatroomId") Long chatroomId,
			@RequestParam("chatroomName") String chatroomName) {
		return ResponseEntity.ok(chatService.updateChatroomName(chatroomId, chatroomName));
	}

	@DeleteMapping("/leaveChatroom")
	public ResponseEntity<ResponsePayload> leaveChatroom(@RequestParam("username") String username,
			@RequestParam("chatroomId") Long chatroomId) {
		return ResponseEntity.ok(chatService.leaveChatroom(username, chatroomId));
	}

	@DeleteMapping("/removeMemberFromChatroom")
	public ResponseEntity<ResponsePayload> removeMemberFromChatroom(@RequestParam("username") String username,
			@RequestParam("chatroomId") Long chatroomId,
			@RequestParam("usernameOfMemberToRemove") String usernameOfMemberToRemove) {
		return ResponseEntity.ok(chatService.removeMemberFromChatroom(username, chatroomId, usernameOfMemberToRemove));
		// TODO: make it for groups only
	}

}
