package com.stevenst.app.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.stevenst.app.exception.IgorFriendRequestException;
import com.stevenst.lib.exception.IgorUserNotFoundException;
import com.stevenst.app.model.FriendRequests;
import com.stevenst.app.model.Friendships;
import com.stevenst.lib.payload.ResponsePayload;
import com.stevenst.app.repository.FriendRequestsRepository;
import com.stevenst.app.repository.FriendshipsRepository;
import com.stevenst.app.repository.NotificationRepository;
import com.stevenst.app.repository.UserRepository;
import com.stevenst.app.service.FriendsService;
import com.stevenst.lib.model.Notification;
import com.stevenst.lib.model.User;
import com.stevenst.lib.model.enums.NotificationType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FriendsServiceImpl implements FriendsService {
	private final UserRepository userRepository;
	private final FriendRequestsRepository friendRequestsRepository;
	private final FriendshipsRepository friendshipsRepository;
	private final NotificationRepository notificationRepository;

	@Override
	public ResponseEntity<ResponsePayload> sendFriendRequest(String senderUsername, String receiverUsername) {
		User sender = userRepository.findByUsername(senderUsername)
				.orElseThrow(
						() -> new IgorUserNotFoundException("User with username " + senderUsername + " not found"));
		User receiver = userRepository.findByUsername(receiverUsername)
				.orElseThrow(
						() -> new IgorUserNotFoundException("User with username " + receiverUsername + " not found"));

		if (sender.equals(receiver)) {
			throw new IgorFriendRequestException(
					"Cannot send a friend request to oneself (" + senderUsername + ")");
		}

		if (friendshipsRepository.existsByUsers(sender, receiver)) {
			throw new IgorFriendRequestException(
					"Cannot send a friend request when already friends (from " + senderUsername + " to "
							+ receiverUsername
							+ ")");
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

		friendRequestsRepository.save(Objects.requireNonNull(
				FriendRequests.builder()
						.sender(sender)
						.receiver(receiver)
						.build()));

		removeNotificationsOfFriends(sender, receiver);
		notificationRepository.save(Objects.requireNonNull(
				Notification.builder()
						.receiver(receiver)
						.emitter(sender)
						.type(NotificationType.FRIEND_REQUEST)
						.description(senderUsername + " sent you a friend request")
						.read(false)
						.build()));

		return ResponseEntity.ok(ResponsePayload.builder().status(200)
				.message("Friend request sent successfully (from " + senderUsername + " to " + receiverUsername + ")")
				.build());
	}

	@Override
	public ResponseEntity<ResponsePayload> checkFriendRequest(String senderUsername, String receiverUsername) {
		User sender = userRepository.findByUsername(senderUsername)
				.orElseThrow(
						() -> new IgorUserNotFoundException("User with username " + senderUsername + " not found"));
		User receiver = userRepository.findByUsername(receiverUsername)
				.orElseThrow(
						() -> new IgorUserNotFoundException("User with username " + receiverUsername + " not found"));

		if (friendRequestsRepository.existsBySenderAndReceiver(sender, receiver)) {
			return ResponseEntity.ok(ResponsePayload.builder().status(200)
					.message("Friend request found (from " + senderUsername + " to " + receiverUsername + ")")
					.build());
		} else {
			return ResponseEntity.ok(ResponsePayload.builder().status(404)
					.message("No friend request found (from " + senderUsername + " to " + receiverUsername + ")")
					.build());
		}
	}

	@Override
	public ResponseEntity<ResponsePayload> checkFriendship(String user1Username, String user2Username) {
		User user1 = userRepository.findByUsername(user1Username)
				.orElseThrow(() -> new IgorUserNotFoundException("User with username " + user1Username + " not found"));
		User user2 = userRepository.findByUsername(user2Username)
				.orElseThrow(() -> new IgorUserNotFoundException("User with username " + user2Username + " not found"));

		if (friendshipsRepository.existsByUsers(user1, user2)) {
			return ResponseEntity.ok(ResponsePayload.builder().status(200)
					.message("Friendship found (between " + user1Username + " and " + user2Username + ")").build());
		} else {
			return ResponseEntity.ok(ResponsePayload.builder().status(404)
					.message("No friendship found (between " + user1Username + " and " + user2Username + ")").build());
		}
	}

	@Override
	public ResponseEntity<List<String>> getAllFriendsUsernames(String username) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new IgorUserNotFoundException("User with username " + username + " not found"));

		List<Friendships> friendships = friendshipsRepository.findAllByUser(user);

		List<String> friendUsernames = friendships.stream().map(friendship -> {
			User friend = user.equals(friendship.getUser1()) ? friendship.getUser2() : friendship.getUser1();
			return friend.getUsername();
		}).collect(Collectors.toList());

		return ResponseEntity.ok(friendUsernames);
	}

	@Override
	public ResponseEntity<ResponsePayload> acceptFriendRequest(String senderUsername, String receiverUsername) {
		User sender = userRepository.findByUsername(senderUsername)
				.orElseThrow(
						() -> new IgorUserNotFoundException("User with username " + senderUsername + " not found"));
		User receiver = userRepository.findByUsername(receiverUsername)
				.orElseThrow(
						() -> new IgorUserNotFoundException("User with username " + receiverUsername + " not found"));

		if (friendRequestsRepository.existsBySenderAndReceiver(receiver, sender)) {
			throw new IgorFriendRequestException(
					"Cannot accept one's own friend request (from " + receiverUsername + " to " + senderUsername + ")");
		}

		if (!friendRequestsRepository.existsBySenderAndReceiver(sender, receiver)) {
			throw new IgorFriendRequestException("Cannot accept friend request when no friend request found (from "
					+ senderUsername + " to " + receiverUsername + ")");
		}

		friendRequestsRepository.deleteBySenderAndReceiver(sender, receiver);
		friendshipsRepository.save(Objects.requireNonNull(
				Friendships.builder()
						.user1(sender)
						.user2(receiver)
						.build()));

		removeNotificationsOfFriends(sender, receiver);
		notificationRepository.save(Objects.requireNonNull(
				Notification.builder()
						.receiver(sender)
						.emitter(receiver)
						.type(NotificationType.FRIEND_REQUEST_ACCEPTED)
						.description(receiverUsername + " accepted your friend request")
						.read(false)
						.build()));

		return ResponseEntity
				.ok(ResponsePayload.builder().status(200).message("Friend request accepted successfully (from "
						+ senderUsername + " accepted by " + receiverUsername + ")").build());
	}

	@Override
	public ResponseEntity<ResponsePayload> cancelFriendRequest(String senderUsername, String receiverUsername) {
		User sender = userRepository.findByUsername(senderUsername)
				.orElseThrow(
						() -> new IgorUserNotFoundException("User with username " + senderUsername + " not found"));
		User receiver = userRepository.findByUsername(receiverUsername)
				.orElseThrow(
						() -> new IgorUserNotFoundException("User with username " + receiverUsername + " not found"));

		if (friendRequestsRepository.existsBySenderAndReceiver(receiver, sender)) {
			throw new IgorFriendRequestException("Cannot cancel someone else's friend request (from " + receiverUsername
					+ " to " + senderUsername + ")");
		}

		if (!friendRequestsRepository.existsBySenderAndReceiver(sender, receiver)) {
			throw new IgorFriendRequestException("Cannot cancel friend request when no friend request found (from "
					+ senderUsername + " to " + receiverUsername + ")");
		}

		friendRequestsRepository.deleteBySenderAndReceiver(sender, receiver);

		removeNotificationsOfFriends(sender, receiver);

		return ResponseEntity.ok(ResponsePayload.builder().status(200).message(
				"Friend request canceled successfully (from " + senderUsername + " to " + receiverUsername + ")")
				.build());
	}

	@Override
	public ResponseEntity<ResponsePayload> declineFriendRequest(String senderUsername, String receiverUsername) {
		User sender = userRepository.findByUsername(senderUsername)
				.orElseThrow(
						() -> new IgorUserNotFoundException("User with username " + senderUsername + " not found"));
		User receiver = userRepository.findByUsername(receiverUsername)
				.orElseThrow(
						() -> new IgorUserNotFoundException("User with username " + receiverUsername + " not found"));

		if (friendRequestsRepository.existsBySenderAndReceiver(receiver, sender)) {
			throw new IgorFriendRequestException("Cannot decline one's own friend request (from " + receiverUsername
					+ " to " + senderUsername + ")");
		}

		if (!friendRequestsRepository.existsBySenderAndReceiver(sender, receiver)) {
			throw new IgorFriendRequestException("Cannot decline friend request when no friend request found (from "
					+ senderUsername + " to " + receiverUsername + ")");
		}

		friendRequestsRepository.deleteBySenderAndReceiver(sender, receiver);

		removeNotificationsOfFriends(sender, receiver);
		notificationRepository.save(Objects.requireNonNull(
				Notification.builder()
						.receiver(sender)
						.emitter(receiver)
						.type(NotificationType.FRIEND_REQUEST_DECLINED)
						.description(receiverUsername + " declined your friend request")
						.read(false)
						.build()));

		return ResponseEntity.ok(ResponsePayload.builder().status(200).message(
				"Friend request declined successfully (from " + senderUsername + " to " + receiverUsername + ")")
				.build());
	}

	@Override
	public ResponseEntity<ResponsePayload> unfriend(String unfrienderUsername, String unfriendedUsername) {
		User unfriender = userRepository.findByUsername(unfrienderUsername)
				.orElseThrow(
						() -> new IgorUserNotFoundException("User with username " + unfrienderUsername + " not found"));
		User unfriended = userRepository.findByUsername(unfriendedUsername)
				.orElseThrow(
						() -> new IgorUserNotFoundException("User with username " + unfriendedUsername + " not found"));

		if (!friendshipsRepository.existsByUsers(unfriender, unfriended)) {
			throw new IgorFriendRequestException(
					"Cannot unfriend when no friendship found (between " + unfrienderUsername
							+ " and " + unfriendedUsername + ")");
		}

		friendshipsRepository.deleteByUsers(unfriender, unfriended);

		removeNotificationsOfFriends(unfriender, unfriended);
		notificationRepository.save(Objects.requireNonNull(
				Notification.builder()
						.receiver(unfriended)
						.emitter(unfriender)
						.type(NotificationType.UNFRIEND)
						.description(unfrienderUsername + " unfriended you")
						.read(false)
						.build()));

		return ResponseEntity.ok(ResponsePayload.builder().status(200).message(
				"Unfriend successfully done (" + unfrienderUsername + " unfriended " + unfriendedUsername + ")")
				.build());
	}

	// ----------------------------------------------------------------------

	private void removeNotificationsOfFriends(User sender, User receiver) {
		notificationRepository.deleteByReceiverAndEmitterFriendship(receiver, sender,
				List.of(NotificationType.FRIEND_REQUEST, NotificationType.FRIEND_REQUEST_ACCEPTED,
						NotificationType.FRIEND_REQUEST_DECLINED, NotificationType.UNFRIEND));

		notificationRepository.deleteByReceiverAndEmitterFriendship(sender, receiver,
				List.of(NotificationType.FRIEND_REQUEST, NotificationType.FRIEND_REQUEST_ACCEPTED,
						NotificationType.FRIEND_REQUEST_DECLINED, NotificationType.UNFRIEND));
	}

}
