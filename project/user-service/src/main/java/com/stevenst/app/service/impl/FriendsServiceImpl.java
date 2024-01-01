package com.stevenst.app.service.impl;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.stevenst.app.exception.IgorFriendRequestException;
import com.stevenst.app.exception.IgorNotFoundException;
import com.stevenst.app.model.FriendRequest;
import com.stevenst.app.model.Friendship;
import com.stevenst.app.payload.MessagePayload;
import com.stevenst.app.repository.FriendRequestRepository;
import com.stevenst.app.repository.FriendshipRepository;
import com.stevenst.app.repository.UserRepository;
import com.stevenst.app.service.FriendsService;
import com.stevenst.lib.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FriendsServiceImpl implements FriendsService {
	private final UserRepository userRepository;
	private final FriendRequestRepository friendRequestRepository;
	private final FriendshipRepository friendshipRepository;

	public ResponseEntity<MessagePayload> sendFriendRequest(String senderUsername, String receiverUsername) {
		User sender = userRepository.findByUsername(senderUsername)
				.orElseThrow(() -> new IgorNotFoundException("User with username " + senderUsername + " not found."));
		User receiver = userRepository.findByUsername(receiverUsername)
				.orElseThrow(() -> new IgorNotFoundException("User with username " + receiverUsername + " not found."));

		if (friendRequestRepository.existsBySenderAndReceiver(sender, receiver)) {
			throw new IgorFriendRequestException(
					"Cannot send a friend request twice (from " + senderUsername + " to " + receiverUsername + ")");
		}

		if (friendRequestRepository.existsBySenderAndReceiver(receiver, sender)) {
			throw new IgorFriendRequestException(
					"Cannot send a friend request when having one already received (from " + receiverUsername
							+ " to " + senderUsername + ")");
		}

		if (friendshipRepository.existsByUsers(sender, receiver)) {
			throw new IgorFriendRequestException(
					"Cannot send a friend request when already friends");
		}

		friendRequestRepository.save(FriendRequest.builder()
				.sender(sender)
				.receiver(receiver)
				.build());

		return ResponseEntity.ok(MessagePayload.builder()
				.message("Friend request sent successfully from " + senderUsername + " to " + receiverUsername + ".")
				.build());
	}

	public ResponseEntity<MessagePayload> acceptFriendRequest(String senderUsername, String receiverUsername) {
		User sender = userRepository.findByUsername(senderUsername)
				.orElseThrow(() -> new IgorNotFoundException("User with username " + senderUsername + " not found."));
		User receiver = userRepository.findByUsername(receiverUsername)
				.orElseThrow(() -> new IgorNotFoundException("User with username " + receiverUsername + " not found."));

		if (!friendRequestRepository.existsBySenderAndReceiver(sender, receiver)) {
			throw new IgorFriendRequestException(
					"Cannot accept friend request when no friend request found from " + senderUsername + " to "
							+ receiverUsername);
		}

		if (friendRequestRepository.existsBySenderAndReceiver(receiver, sender)) {
			throw new IgorFriendRequestException(
					receiverUsername + " already sent a friend request to " + senderUsername);
		}

		if (friendshipRepository.existsByUsers(sender, receiver)) {
			throw new IgorFriendRequestException(
					"Friendship already exists between " + senderUsername + " and " + receiverUsername);
		}

		friendRequestRepository.deleteBySenderAndReceiver(sender, receiver);

		friendshipRepository.save(Friendship.builder()
				.user1(sender)
				.user2(receiver)
				.build());

		return ResponseEntity.ok(MessagePayload.builder()
				.message(
						"Friend request from " + senderUsername + " accepted successfully by " + receiverUsername + ".")
				.build());
	}
}
