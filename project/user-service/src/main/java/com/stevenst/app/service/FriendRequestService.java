package com.stevenst.app.service;

import org.springframework.http.ResponseEntity;

public interface FriendRequestService {
	ResponseEntity<String> sendFriendRequest(String from, String to);
}
