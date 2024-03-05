package com.stevenst.app.service;

import java.util.List;

import com.stevenst.app.payload.NotificationFPayload;

public interface NotificationFService {
	public List<NotificationFPayload> getLast50NotificationsOfFriends(String username);
}
