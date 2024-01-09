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

import com.stevenst.app.controller.api.FriendsApi;
import com.stevenst.lib.payload.ResponsePayload;
import com.stevenst.app.service.FriendsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/friend")
@RequiredArgsConstructor
public class FriendsController implements FriendsApi {
	private final FriendsService friendsService;

	@PostMapping("/sendRequest")
	public ResponseEntity<ResponsePayload> sendFriendRequest(@RequestParam String sender,
			@RequestParam String receiver) {
		return friendsService.sendFriendRequest(sender, receiver);
	}

	@GetMapping("/checkRequest")
	public ResponseEntity<ResponsePayload> checkFriendRequest(@RequestParam String sender,
			@RequestParam String receiver) {
		return friendsService.checkFriendRequest(sender, receiver);
	}

	@GetMapping("/checkFriendship")
	public ResponseEntity<ResponsePayload> checkFriendship(@RequestParam String user1, @RequestParam String user2) {
		return friendsService.checkFriendship(user1, user2);
	}

	@GetMapping("/getAllFriendsUsernames")
	public ResponseEntity<List<String>> getAllFriendsUsernames(@RequestParam String username) {
		return friendsService.getAllFriendsUsernames(username);
	}

	@PutMapping("/acceptRequest")
	public ResponseEntity<ResponsePayload> acceptFriendRequest(@RequestParam String sender,
			@RequestParam String receiver) {
		return friendsService.acceptFriendRequest(sender, receiver);
	}

	@DeleteMapping("/cancelRequest")
	public ResponseEntity<ResponsePayload> cancelFriendRequest(@RequestParam String sender,
			@RequestParam String receiver) {
		return friendsService.cancelFriendRequest(sender, receiver);
	}

	@DeleteMapping("/declineRequest")
	public ResponseEntity<ResponsePayload> declineFriendRequest(@RequestParam String sender,
			@RequestParam String receiver) {
		return friendsService.declineFriendRequest(sender, receiver);
	}

	@DeleteMapping("/unfriend")
	public ResponseEntity<ResponsePayload> unfriend(@RequestParam String unfriender, @RequestParam String unfriended) {
		return friendsService.unfriend(unfriender, unfriended);
	}
}
