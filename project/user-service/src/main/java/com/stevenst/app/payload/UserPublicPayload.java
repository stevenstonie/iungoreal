package com.stevenst.app.payload;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class UserPublicPayload {
	private String username;
	private LocalDateTime createdAt;
}
