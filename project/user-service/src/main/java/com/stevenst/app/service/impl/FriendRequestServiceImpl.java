package com.stevenst.app.service.impl;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.stevenst.app.repository.UserRepository;
import com.stevenst.app.service.FriendRequestService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FriendRequestServiceImpl implements FriendRequestService {
	private final UserRepository userRepository;

	public ResponseEntity<String> sendFriendRequest(String from, String to) {
		return null;
	}
}
