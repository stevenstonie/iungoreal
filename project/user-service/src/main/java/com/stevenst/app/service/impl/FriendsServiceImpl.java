package com.stevenst.app.service.impl;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.stevenst.app.exception.IgorFriendRequestException;
import com.stevenst.app.exception.IgorNotFoundException;
import com.stevenst.app.model.FriendRequests;
import com.stevenst.app.model.Friendships;
import com.stevenst.app.payload.MessagePayload;
import com.stevenst.app.repository.FriendRequestsRepository;
import com.stevenst.app.repository.FriendshipsRepository;
import com.stevenst.app.repository.UserRepository;
import com.stevenst.app.service.FriendsService;
import com.stevenst.lib.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FriendsServiceImpl implements FriendsService {
	private final UserRepository userRepository;
	private final FriendRequestsRepository friendRequestsRepository;
	private final FriendshipsRepository friendshipsRepository;

	public ResponseEntity<MessagePayload> sendFriendRequest(String senderUsername, String receiverUsername) {
		User sender = userRepository.findByUsername(senderUsername)
				.orElseThrow(() -> new IgorNotFoundException("User with username " + senderUsername + " not found."));
		User receiver = userRepository.findByUsername(receiverUsername)
				.orElseThrow(() -> new IgorNotFoundException("User with username " + receiverUsername + " not found."));

		if (sender.equals(receiver)) {
			throw new IgorFriendRequestException(
					"Cannot send a friend request to oneself");
		}

		if (friendRequestsRepository.existsBySenderAndReceiver(sender, receiver)) {
			throw new IgorFriendRequestException(
					"Cannot send a friend request twice (from " + senderUsername + " to " + receiverUsername + ")");
		}

		if (friendRequestsRepository.existsBySenderAndReceiver(receiver, sender)) {
			throw new IgorFriendRequestException(
					"Cannot send a friend request when having one already received (from " + receiverUsername
							+ " to " + senderUsername + ")");
		}

		if (friendshipsRepository.existsByUsers(sender, receiver)) {
			throw new IgorFriendRequestException(
					"Cannot send a friend request when already friends");
		}

		friendRequestsRepository.save(FriendRequests.builder()
				.sender(sender)
				.receiver(receiver)
				.build());

		return ResponseEntity.ok(MessagePayload.builder().success(true)
				.message("Friend request sent successfully from " + senderUsername + " to " + receiverUsername + ".")
				.build());
	}

	public ResponseEntity<MessagePayload> acceptFriendRequest(String senderUsername, String receiverUsername) {
		User sender = userRepository.findByUsername(senderUsername)
				.orElseThrow(() -> new IgorNotFoundException("User with username " + senderUsername + " not found."));
		User receiver = userRepository.findByUsername(receiverUsername)
				.orElseThrow(() -> new IgorNotFoundException("User with username " + receiverUsername + " not found."));

		if (!friendRequestsRepository.existsBySenderAndReceiver(sender, receiver)) {
			throw new IgorFriendRequestException(
					"Cannot accept friend request when no friend request found from " + senderUsername + " to "
							+ receiverUsername);
		}

		if (friendRequestsRepository.existsBySenderAndReceiver(receiver, sender)) {
			throw new IgorFriendRequestException(
					receiverUsername + " already sent a friend request to " + senderUsername);
		}

		if (friendshipsRepository.existsByUsers(sender, receiver)) {
			throw new IgorFriendRequestException(
					"Friendship already exists between " + senderUsername + " and " + receiverUsername);
		}

		friendRequestsRepository.deleteBySenderAndReceiver(sender, receiver);

		friendshipsRepository.save(Friendships.builder()
				.user1(sender)
				.user2(receiver)
				.build());

		return ResponseEntity.ok(MessagePayload.builder().success(true)
				.message(
						"Friend request from " + senderUsername + " accepted successfully by " + receiverUsername + ".")
				.build());
	}

	public ResponseEntity<MessagePayload> checkFriendRequest(String senderUsername, String receiverUsername) {
		User sender = userRepository.findByUsername(senderUsername)
				.orElseThrow(() -> new IgorNotFoundException("User with username " + senderUsername + " not found."));
		User receiver = userRepository.findByUsername(receiverUsername)
				.orElseThrow(() -> new IgorNotFoundException("User with username " + receiverUsername + " not found."));

		if (friendRequestsRepository.existsBySenderAndReceiver(sender, receiver)) {
			return ResponseEntity.ok(MessagePayload.builder().success(true)
					.message("Friend request found from " + senderUsername + " to " + receiverUsername + ".")
					.build());
		} else {
			return ResponseEntity.ok(MessagePayload.builder().success(false)
					.message("No friend request found from " + senderUsername + " to " + receiverUsername + ".")
					.build());
		}
	}

	public ResponseEntity<MessagePayload> checkFriendship(String user1Username, String user2Username) {
		User user1 = userRepository.findByUsername(user1Username)
				.orElseThrow(() -> new IgorNotFoundException("User with username " + user1Username + " not found."));
		User user2 = userRepository.findByUsername(user2Username)
				.orElseThrow(() -> new IgorNotFoundException("User with username " + user2Username + " not found."));

		if (friendshipsRepository.existsByUsers(user1, user2)) {
			return ResponseEntity.ok(MessagePayload.builder().success(true)
					.message("Friendship found between " + user1Username + " and " + user2Username + ".")
					.build());
		} else {
			return ResponseEntity.ok(MessagePayload.builder().success(false)
					.message("No friendship found between " + user1Username + " and " + user2Username + ".")
					.build());
		}
	}

	@Override
	public ResponseEntity<MessagePayload> cancelFriendRequest(String senderUsername, String receiverUsername) {
		System.out.println("unimplemented method");
		return null;
	}

	@Override
	public ResponseEntity<MessagePayload> declineFriendRequest(String senderUsername, String receiverUsername) {
		System.out.println("unimplemented method");
		return null;
	}

	@Override
	public ResponseEntity<MessagePayload> unfriend(String user1Username, String user2Username) {
		System.out.println("unimplemented method");
		return null;
	}

	
}
