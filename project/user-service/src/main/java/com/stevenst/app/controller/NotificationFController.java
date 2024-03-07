package com.stevenst.app.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stevenst.app.payload.NotificationFPayload;
import com.stevenst.app.service.NotificationFService;
import com.stevenst.lib.payload.ResponsePayload;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/notification/friend")
@RequiredArgsConstructor
public class NotificationFController {
	private final NotificationFService notificationFService;

	@GetMapping("/getLast50")
	public ResponseEntity<List<NotificationFPayload>> getLast50NotificationsOfFriends(@RequestParam String username) {
		return ResponseEntity.ok(notificationFService.getLast50NotificationsOfFriends(username));
	}

	@DeleteMapping("remove")
	public ResponseEntity<ResponsePayload> removeNotificationF(@RequestParam Long id) {
		return ResponseEntity.ok(notificationFService.removeNotificationF(id));
	}

	@GetMapping("/countLast51")
	public ResponseEntity<Integer> countLast51NotificationsOfFriends(@RequestParam String username) {
		return ResponseEntity.ok(notificationFService.countLast51NotificationsOfFriends(username));
	}
}
