package com.stevenst.app.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.stevenst.app.payload.NotificationFPayload;
import com.stevenst.app.repository.NotificationRepository;
import com.stevenst.app.repository.UserRepository;
import com.stevenst.app.service.NotificationFService;
import com.stevenst.app.service.UserService;
import com.stevenst.app.util.JsonUtil;
import com.stevenst.lib.exception.IgorEntityNotFoundException;
import com.stevenst.lib.exception.IgorNullValueException;
import com.stevenst.lib.exception.IgorUserNotFoundException;
import com.stevenst.lib.model.Notification;
import com.stevenst.lib.model.User;
import com.stevenst.lib.model.enums.NotificationType;
import com.stevenst.lib.payload.ResponsePayload;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationFServiceImpl implements NotificationFService {
	private final NotificationRepository notificationRepository;
	private final UserRepository userRepository;
	// private final UserService userService;		// (1)

	public List<NotificationFPayload> getLast50NotificationsF(String username) {
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
							// .emitterPfpLink(			// (1)
							// 		JsonUtil.getStringFromJson(userService
							// 				.getPfpPreSignedLinkFromS3(notification.getEmitter().getUsername())))
							.type(notification.getType())
							.description(notification.getDescription())
							.createdAt(notification.getCreatedAt())
							.build();
					notifications.add(payload);
				});

		return notifications;
	}

	public ResponsePayload removeNotificationF(Long id) {
		if (id == null) {
			throw new IgorNullValueException("Cannot remove notification as the value of id is null.");
		}
		Notification notification = notificationRepository.findById(id)
				.orElseThrow(() -> new IgorEntityNotFoundException("Notification not found (of id: " + id + ")."));

		notificationRepository.delete(Objects.requireNonNull(notification));
		return ResponsePayload.builder().status(200).message("Notification successfully removed.").build();
	}

	public Integer countLast51NotificationsF(String username) {
		User user = userRepository.findByUsername(username).orElseThrow(
				() -> new IgorUserNotFoundException("User not found (with username: " + username + ")"));

		Integer countOfNotificationsF = notificationRepository.countLast51NotificationsF(user,
				List.of(NotificationType.FRIEND_REQUEST, NotificationType.FRIEND_REQUEST_ACCEPTED,
						NotificationType.FRIEND_REQUEST_DECLINED, NotificationType.UNFRIEND));
		if (countOfNotificationsF == null) {
			return 0;
		}

		return countOfNotificationsF;
	}
}
// (1) -> only uncomment when you want to access the cloud storage
// dev comm
