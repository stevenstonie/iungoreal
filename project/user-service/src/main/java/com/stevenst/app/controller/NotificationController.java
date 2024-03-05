package com.stevenst.app.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stevenst.app.payload.NotificationFPayload;
import com.stevenst.app.service.NotificationFService;
import com.stevenst.lib.model.Notification;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/notification/friend")
@RequiredArgsConstructor
public class NotificationController {
	private final NotificationFService notificationFService;

	@GetMapping("/getLast50")
	public ResponseEntity<List<NotificationFPayload>> getLast50NotificationsOfFriends(@RequestParam String username) {
		return ResponseEntity.ok(notificationFService.getLast50NotificationsOfFriends(username));
	}
	
}
