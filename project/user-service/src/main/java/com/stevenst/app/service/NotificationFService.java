package com.stevenst.app.service;

import java.util.List;

import com.stevenst.app.payload.NotificationFPayload;
import com.stevenst.lib.payload.ResponsePayload;

public interface NotificationFService {
	public List<NotificationFPayload> getLast50NotificationsOfFriends(String username);

	public ResponsePayload removeNotificationF(Long id);

	public Integer countLast51NotificationsOfFriends(String username);
}
