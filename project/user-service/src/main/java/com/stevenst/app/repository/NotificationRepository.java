package com.stevenst.app.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.stevenst.lib.model.Notification;
import com.stevenst.lib.model.User;
import com.stevenst.lib.model.enums.NotificationType;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
	@Transactional
	@Modifying
	@Query("DELETE FROM Notification n WHERE n.receiver = :receiver AND n.emitter = :emitter AND n.type IN :types")
	void deleteByReceiverAndEmitterFriendship(@Param("receiver") User receiver, @Param("emitter") User emitter,
			@Param("types") List<NotificationType> types);
	
	@Query("SELECT n FROM Notification n WHERE n.receiver = :receiver AND n.type IN :types ORDER BY n.id DESC")
	List<Notification> getLast50Friendship(@Param("receiver") User receiver, @Param("types") List<NotificationType> types, Pageable pageable);
}
