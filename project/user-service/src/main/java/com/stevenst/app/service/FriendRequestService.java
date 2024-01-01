package com.stevenst.app.service;

import org.springframework.http.ResponseEntity;

import com.stevenst.app.payload.ResponsePayload;

public interface FriendRequestService {
	ResponseEntity<ResponsePayload> sendFriendRequest(String from, String to);
}
