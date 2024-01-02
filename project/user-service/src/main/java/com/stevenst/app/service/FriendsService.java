package com.stevenst.app.service;

import org.springframework.http.ResponseEntity;

import com.stevenst.app.payload.MessagePayload;

public interface FriendsService {
	ResponseEntity<MessagePayload> sendFriendRequest(String senderUsername, String receiverUsername);

	ResponseEntity<MessagePayload> acceptFriendRequest(String senderUsername, String receiverUsername);

	ResponseEntity<MessagePayload> checkFriendRequest(String senderUsername, String receiverUsername);

	ResponseEntity<MessagePayload> checkFriendship(String user1Username, String user2Username);

	ResponseEntity<MessagePayload> cancelFriendRequest(String senderUsername, String receiverUsername);

	ResponseEntity<MessagePayload> declineFriendRequest(String senderUsername, String receiverUsername);

	ResponseEntity<MessagePayload> unfriend(String user1Username, String user2Username);
}
