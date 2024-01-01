package com.stevenst.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stevenst.app.controller.api.FriendRequestApi;
import com.stevenst.app.payload.MessagePayload;
import com.stevenst.app.service.FriendRequestService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendRequestController implements FriendRequestApi {
	private final FriendRequestService friendRequestService;

	@PostMapping("/sendRequest")
	public ResponseEntity<MessagePayload> sendFriendRequest(@RequestParam String from, @RequestParam String to) {
		return friendRequestService.sendFriendRequest(from, to);
	}
}
