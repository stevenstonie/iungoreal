package com.stevenst.app.service;

import org.springframework.http.ResponseEntity;

import com.stevenst.app.payload.MessagePayload;

public interface FriendRequestService {
	ResponseEntity<MessagePayload> sendFriendRequest(String from, String to);
}
