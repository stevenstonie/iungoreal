package com.stevenst.app.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.stevenst.lib.payload.ResponsePayload;

public interface FriendsService {
	ResponseEntity<ResponsePayload> sendFriendRequest(String senderUsername, String receiverUsername);

	ResponseEntity<ResponsePayload> checkFriendRequest(String senderUsername, String receiverUsername);

	ResponseEntity<ResponsePayload> checkFriendship(String user1Username, String user2Username);

	ResponseEntity<List<String>> getAllFriendsUsernames(String username);

	ResponseEntity<ResponsePayload> acceptFriendRequest(String senderUsername, String receiverUsername);

	ResponseEntity<ResponsePayload> cancelFriendRequest(String senderUsername, String receiverUsername);

	ResponseEntity<ResponsePayload> declineFriendRequest(String senderUsername, String receiverUsername);

	ResponseEntity<ResponsePayload> unfriend(String user1Username, String user2Username);
}
