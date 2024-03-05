package com.stevenst.app.service;

import java.util.List;

import com.stevenst.app.payload.NotificationFPayload;
import com.stevenst.lib.model.Notification;

public interface NotificationFService {
	public List<NotificationFPayload> getLast50NotificationsOfFriends(String username);
}
