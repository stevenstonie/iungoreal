package com.stevenst.app.payload;

import java.time.LocalDateTime;

import com.stevenst.lib.model.enums.NotificationType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationFPayload {
	private Long id;
	private String receiverUsername;
	private String emitterUsername;
	private String emitterPfpLink;
	private NotificationType type;
	private String description;
	private LocalDateTime createdAt;
}
