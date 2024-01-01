package com.stevenst.app.service.impl;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.stevenst.app.exception.IgorFriendRequestException;
import com.stevenst.app.exception.IgorNotFoundException;
import com.stevenst.app.model.FriendRequests;
import com.stevenst.app.payload.MessagePayload;
import com.stevenst.app.repository.FriendRequestsRepository;
import com.stevenst.app.repository.UserRepository;
import com.stevenst.app.service.FriendRequestService;
import com.stevenst.lib.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FriendRequestServiceImpl implements FriendRequestService {
	private final UserRepository userRepository;
	private final FriendRequestsRepository friendRequestsRepository;

	public ResponseEntity<MessagePayload> sendFriendRequest(String from, String to) {
		User sender = userRepository.findByUsername(from)
				.orElseThrow(() -> new IgorNotFoundException("User with username " + from + " not found."));
		User receiver = userRepository.findByUsername(to)
				.orElseThrow(() -> new IgorNotFoundException("User with username " + to + " not found."));

		if (friendRequestsRepository.existsBySenderIdAndReceiverId(sender, receiver)) {
			throw new IgorFriendRequestException("Friend request already exists between " + from + " and " + to);
		}
		
		if (friendRequestsRepository.existsBySenderIdAndReceiverId(receiver, sender)) {
			throw new IgorFriendRequestException("Friend request already exists between " + to + " and " + from);
		}

		friendRequestsRepository.save(FriendRequests.builder()
				.senderId(sender)
				.receiverId(receiver)
				.build());
		
		return ResponseEntity.ok(MessagePayload.builder()
				.message("Friend request sent successfully.")
				.build());
	}
}
