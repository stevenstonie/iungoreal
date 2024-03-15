package com.stevenst.app.payload;

import java.time.LocalDateTime;

import com.stevenst.lib.model.Region;
import com.stevenst.lib.model.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class UserPrivatePayload {
	private Long id;
	private String email;
	private String username;
	private Role role;
	private Long primaryRegionId;
	private LocalDateTime createdAt;
}
