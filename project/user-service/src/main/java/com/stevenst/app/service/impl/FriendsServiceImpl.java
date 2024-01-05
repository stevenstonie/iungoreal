package com.stevenst.app.service.impl;

import java.util.List;
import java.util.stream.Collectors;

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

	@Override
	public ResponseEntity<MessagePayload> sendFriendRequest(String senderUsername, String receiverUsername) {
		User sender = userRepository.findByUsername(senderUsername)
				.orElseThrow(() -> new IgorNotFoundException("User with username " + senderUsername + " not found"));
		User receiver = userRepository.findByUsername(receiverUsername)
				.orElseThrow(() -> new IgorNotFoundException("User with username " + receiverUsername + " not found"));

		if (sender.equals(receiver)) {
			throw new IgorFriendRequestException(
					"Cannot send a friend request to oneself");
		}

		if (friendshipsRepository.existsByUsers(sender, receiver)) {
			throw new IgorFriendRequestException(
					"Cannot send a friend request when already friends");
		}

		if (friendRequestsRepository.existsBySenderAndReceiver(receiver, sender)) {
			throw new IgorFriendRequestException(
					"Cannot send a friend request when having one already received (from " + receiverUsername
							+ " to " + senderUsername + ")");
		}

		if (friendRequestsRepository.existsBySenderAndReceiver(sender, receiver)) {
			throw new IgorFriendRequestException(
					"Cannot send a friend request twice (from " + senderUsername + " to " + receiverUsername + ")");
		}

		friendRequestsRepository.save(FriendRequests.builder()
				.sender(sender)
				.receiver(receiver)
				.build());

		return ResponseEntity.ok(MessagePayload.builder().success(true)
				.message("Friend request sent successfully (from " + senderUsername + " to " + receiverUsername + ")")
				.build());
	}

	@Override
	public ResponseEntity<MessagePayload> checkFriendRequest(String senderUsername, String receiverUsername) {
		User sender = userRepository.findByUsername(senderUsername)
				.orElseThrow(() -> new IgorNotFoundException("User with username " + senderUsername + " not found"));
		User receiver = userRepository.findByUsername(receiverUsername)
				.orElseThrow(() -> new IgorNotFoundException("User with username " + receiverUsername + " not found"));

		if (friendRequestsRepository.existsBySenderAndReceiver(sender, receiver)) {
			return ResponseEntity.ok(MessagePayload.builder().success(true)
					.message("Friend request found (from " + senderUsername + " to " + receiverUsername + ")")
					.build());
		} else {
			return ResponseEntity.ok(MessagePayload.builder().success(false)
					.message("No friend request found (from " + senderUsername + " to " + receiverUsername + ")")
					.build());
		}
	}

	@Override
	public ResponseEntity<MessagePayload> checkFriendship(String user1Username, String user2Username) {
		User user1 = userRepository.findByUsername(user1Username)
				.orElseThrow(() -> new IgorNotFoundException("User with username " + user1Username + " not found"));
		User user2 = userRepository.findByUsername(user2Username)
				.orElseThrow(() -> new IgorNotFoundException("User with username " + user2Username + " not found"));

		if (friendshipsRepository.existsByUsers(user1, user2)) {
			return ResponseEntity.ok(MessagePayload.builder().success(true)
					.message("Friendship found (between " + user1Username + " and " + user2Username + ")").build());
		} else {
			return ResponseEntity.ok(MessagePayload.builder().success(false)
					.message("No friendship found (between " + user1Username + " and " + user2Username + ")").build());
		}
	}

	@Override
	public ResponseEntity<List<String>> getAllFriendsUsernames(String username) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new IgorNotFoundException("User with username " + username + " not found"));

		List<Friendships> friendships = friendshipsRepository.findAllByUser(user);

		List<String> friendUsernames = friendships.stream().map(friendship -> {
			User friend = user.equals(friendship.getUser1()) ? friendship.getUser2() : friendship.getUser1();
			return friend.getUsername();
		}).collect(Collectors.toList());

		return ResponseEntity.ok(friendUsernames);
	}

	@Override
	public ResponseEntity<MessagePayload> acceptFriendRequest(String senderUsername, String receiverUsername) {
		User sender = userRepository.findByUsername(senderUsername)
				.orElseThrow(() -> new IgorNotFoundException("User with username " + senderUsername + " not found"));
		User receiver = userRepository.findByUsername(receiverUsername)
				.orElseThrow(() -> new IgorNotFoundException("User with username " + receiverUsername + " not found"));

		if (friendRequestsRepository.existsBySenderAndReceiver(receiver, sender)) {
			throw new IgorFriendRequestException(
					"Cannot accept one's own friend request (from " + receiverUsername + " to " + senderUsername + ")");
		}

		if (!friendRequestsRepository.existsBySenderAndReceiver(sender, receiver)) {
			throw new IgorFriendRequestException("Cannot accept friend request when no friend request found (from "
					+ senderUsername + " to " + receiverUsername + ")");
		}

		friendRequestsRepository.deleteBySenderAndReceiver(sender, receiver);
		friendshipsRepository.save(Friendships.builder()
				.user1(sender)
				.user2(receiver)
				.build());

		return ResponseEntity
				.ok(MessagePayload.builder().success(true).message("Friend request accepted successfully (from "
						+ senderUsername + " accepted by " + receiverUsername + ")").build());
	}

	@Override
	public ResponseEntity<MessagePayload> cancelFriendRequest(String senderUsername, String receiverUsername) {
		User sender = userRepository.findByUsername(senderUsername)
				.orElseThrow(() -> new IgorNotFoundException("User with username " + senderUsername + " not found"));
		User receiver = userRepository.findByUsername(receiverUsername)
				.orElseThrow(() -> new IgorNotFoundException("User with username " + receiverUsername + " not found"));

		if (friendRequestsRepository.existsBySenderAndReceiver(receiver, sender)) {
			throw new IgorFriendRequestException("Cannot cancel someone else's friend request (from " + receiverUsername
					+ " to " + senderUsername + ")");
		}

		if (!friendRequestsRepository.existsBySenderAndReceiver(sender, receiver)) {
			throw new IgorFriendRequestException("Cannot cancel friend request when no friend request found (from "
					+ senderUsername + " to " + receiverUsername + ")");
		}

		friendRequestsRepository.deleteBySenderAndReceiver(sender, receiver);

		return ResponseEntity.ok(MessagePayload.builder().success(true).message(
				"Friend request cancelled successfully (from " + senderUsername + " to " + receiverUsername + ")")
				.build());
	}

	@Override
	public ResponseEntity<MessagePayload> declineFriendRequest(String senderUsername, String receiverUsername) {
		User sender = userRepository.findByUsername(senderUsername)
				.orElseThrow(() -> new IgorNotFoundException("User with username " + senderUsername + " not found"));
		User receiver = userRepository.findByUsername(receiverUsername)
				.orElseThrow(() -> new IgorNotFoundException("User with username " + receiverUsername + " not found"));

		if (friendRequestsRepository.existsBySenderAndReceiver(receiver, sender)) {
			throw new IgorFriendRequestException("Cannot decline one's own friend request (from " + receiverUsername
					+ " to " + senderUsername + ")");
		}

		if (!friendRequestsRepository.existsBySenderAndReceiver(sender, receiver)) {
			throw new IgorFriendRequestException("Cannot decline friend request when no friend request found (from "
					+ senderUsername + " to " + receiverUsername + ")");
		}

		friendRequestsRepository.deleteBySenderAndReceiver(sender, receiver);

		return ResponseEntity.ok(MessagePayload.builder().success(true).message(
				"Friend request declined successfully (from " + senderUsername + " to " + receiverUsername + ")")
				.build());
	}

	@Override
	public ResponseEntity<MessagePayload> unfriend(String unfrienderUsername, String unfriendedUsername) {
		User unfriender = userRepository.findByUsername(unfrienderUsername)
				.orElseThrow(
						() -> new IgorNotFoundException("User with username " + unfrienderUsername + " not found"));
		User unfriended = userRepository.findByUsername(unfriendedUsername)
				.orElseThrow(
						() -> new IgorNotFoundException("User with username " + unfriendedUsername + " not found"));

		if (!friendshipsRepository.existsByUsers(unfriender, unfriended)) {
			throw new IgorFriendRequestException(
					"Cannot unfriend when no friendship found (between " + unfrienderUsername
							+ " and " + unfriendedUsername + ")");
		}

		friendshipsRepository.deleteByUsers(unfriender, unfriended);

		return ResponseEntity.ok(MessagePayload.builder().success(true).message(
				"Unfriend successfully done (" + unfrienderUsername + " unfriended " + unfriendedUsername + ")")
				.build());
	}

}
