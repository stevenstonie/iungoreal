package com.stevenst.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stevenst.lib.model.Notification;
import com.stevenst.lib.model.User;
import com.stevenst.lib.model.enums.NotificationType;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
	void deleteByReceiverAndEmitterAndType(User receiver, User emitter, NotificationType type);
}
