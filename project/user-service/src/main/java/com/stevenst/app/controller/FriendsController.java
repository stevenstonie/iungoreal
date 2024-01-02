package com.stevenst.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stevenst.app.controller.api.FriendsApi;
import com.stevenst.app.payload.MessagePayload;
import com.stevenst.app.service.FriendsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendsController implements FriendsApi {
	private final FriendsService friendsService;

	@PostMapping("/sendRequest")
	public ResponseEntity<MessagePayload> sendFriendRequest(@RequestParam String sender,
			@RequestParam String receiver) {
		return friendsService.sendFriendRequest(sender, receiver);
	}

	@PostMapping("/acceptRequest")
	public ResponseEntity<MessagePayload> acceptFriendRequest(@RequestParam String sender,
			@RequestParam String receiver) {
		return friendsService.acceptFriendRequest(sender, receiver);
	}

	@GetMapping("/checkRequest")
	public ResponseEntity<MessagePayload> checkFriendRequest(@RequestParam String sender,
			@RequestParam String receiver) {
		return friendsService.checkFriendRequest(sender, receiver);
	}

	@GetMapping("/checkFriendship")
	public ResponseEntity<MessagePayload> checkFriendship(@RequestParam String user1, @RequestParam String user2) {
		return friendsService.checkFriendship(user1, user2);
	}

	@PostMapping("/cancelRequest")
	public ResponseEntity<MessagePayload> cancelFriendRequest(@RequestParam String sender,
			@RequestParam String receiver) {
		return friendsService.cancelFriendRequest(sender, receiver);
	}

	@PostMapping("/declineRequest")
	public ResponseEntity<MessagePayload> declineFriendRequest(@RequestParam String sender,
			@RequestParam String receiver) {
		return friendsService.declineFriendRequest(sender, receiver);
	}

	@PostMapping("/unfriend")
	public ResponseEntity<MessagePayload> unfriend(@RequestParam String unfriender, @RequestParam String unfriended) {
		return friendsService.unfriend(unfriender, unfriended);
	}
}
