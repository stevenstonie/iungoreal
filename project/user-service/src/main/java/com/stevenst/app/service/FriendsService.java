package com.stevenst.app.service;

import org.springframework.http.ResponseEntity;

import com.stevenst.app.payload.MessagePayload;

public interface FriendsService {
	ResponseEntity<MessagePayload> sendFriendRequest(String senderUsername, String receiverUsername);

	ResponseEntity<MessagePayload> acceptFriendRequest(String senderUsername, String receiverUsername);
}
