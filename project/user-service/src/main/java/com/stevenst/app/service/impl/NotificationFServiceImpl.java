package com.stevenst.app.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.stevenst.app.payload.NotificationFPayload;
import com.stevenst.app.repository.NotificationRepository;
import com.stevenst.app.repository.UserRepository;
import com.stevenst.app.service.NotificationFService;
import com.stevenst.app.service.UserService;
import com.stevenst.lib.exception.IgorUserNotFoundException;
import com.stevenst.lib.model.User;
import com.stevenst.lib.model.enums.NotificationType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationFServiceImpl implements NotificationFService {
	private final NotificationRepository notificationRepository;
	private final UserRepository userRepository;
	private final UserService userService;

	public List<NotificationFPayload> getLast50NotificationsOfFriends(String username) {
		User user = userRepository.findByUsername(username).orElseThrow(
				() -> new IgorUserNotFoundException("User not found (with username: " + username + ")"));

		Pageable pageable = PageRequest.of(0, 50, Sort.by("id").descending());

		List<NotificationFPayload> notifications = new ArrayList<>();
		notificationRepository.getLast50Friendship(user,
				List.of(NotificationType.FRIEND_REQUEST, NotificationType.FRIEND_REQUEST_ACCEPTED,
						NotificationType.FRIEND_REQUEST_DECLINED, NotificationType.UNFRIEND),
				pageable).forEach(notification -> {
					NotificationFPayload payload = NotificationFPayload.builder()
							.id(notification.getId())
							.receiverUsername(notification.getReceiver().getUsername())
							.emitterUsername(notification.getEmitter().getUsername())
							.emitterPfpLink(
									userService.getPfpPreSignedLinkFromS3(notification.getEmitter().getUsername()))
							.type(notification.getType())
							.description(notification.getDescription())
							.createdAt(notification.getCreatedAt())
							.build();
					notifications.add(payload);
				});

		return notifications;
	}
}
