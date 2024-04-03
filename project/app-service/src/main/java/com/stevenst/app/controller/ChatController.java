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

import com.stevenst.app.model.ChatMessage;
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

	@PostMapping("/createDmChatroom")
	public ResponseEntity<ChatroomPayload> createDmChatroom(@RequestParam("username") String username,
			@RequestParam("friendUsername") String friendUsername) {
		return ResponseEntity.ok(chatService.createDmChatroom(username, friendUsername));
	}

	@PostMapping("/createGroupChatroom")
	public ResponseEntity<ChatroomPayload> createGroupChatroom(@RequestParam("username") String username) {
		return ResponseEntity.ok(chatService.createGroupChatroom(username));
	}

	@GetMapping("/getAllDmChatroomsOfUser")
	public ResponseEntity<List<ChatroomPayload>> getAllDmChatroomsOfUser(@RequestParam("username") String username) {
		return ResponseEntity.ok(chatService.getAllDmChatroomsOfUser(username));
	}

	@GetMapping("/getNextMessagesByChatroomId")
	public ResponseEntity<List<ChatMessage>> getNextMessagesByChatroomId(@RequestParam Long chatroomId,
			@RequestParam(required = false) Long cursor,
			@RequestParam(defaultValue = "50") int limit) {
		return ResponseEntity.ok(chatService.getMessagesBeforeCursorByChatroomId(chatroomId, cursor, limit));
	}

	@DeleteMapping("/leaveChatroom")
	public ResponseEntity<ResponsePayload> leaveChatroom(@RequestParam("username") String username,
			@RequestParam("chatroomId") Long chatroomId) {
		return ResponseEntity.ok(chatService.leaveChatroom(username, chatroomId));
	}

	@PutMapping("/updateChatroomName")
	public ResponseEntity<ResponsePayload> updateChatroomName(@RequestParam("chatroomId") Long chatroomId,
			@RequestParam("chatroomName") String chatroomName) {
		return ResponseEntity.ok(chatService.updateChatroomName(chatroomId, chatroomName));
	}
}
