package com.stevenst.app.service;

import java.util.List;

import com.stevenst.lib.model.Notification;

public interface NotificationFService {
	public List<Notification> getLast50NotificationsOfFriends(String username);
}
