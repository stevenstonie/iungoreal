package com.stevenst.app.service.impl;

import org.springframework.stereotype.Service;

import com.stevenst.app.exception.IgorNotFoundException;
import com.stevenst.app.payload.UserPrivatePayload;
import com.stevenst.app.payload.UserPublicPayload;
import com.stevenst.app.repository.UserRepository;
import com.stevenst.app.service.UserService;
import com.stevenst.lib.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private static final String USER_NOT_FOUND = "User not found";
	private final UserRepository userRepository;

	@Override
	public UserPublicPayload getUserPublicByUsername(String username) {
		return userRepository.findByUsername(username)
				.map(user -> UserPublicPayload.builder()
						.username(user.getUsername())
						.createdAt(user.getCreatedAt())
						.build())
				.orElseThrow(() -> new IgorNotFoundException(USER_NOT_FOUND));
	}

	@Override
	public UserPrivatePayload getUserPrivateByUsername(String username) {
		return userRepository.findByUsername(username)
				.map(user -> UserPrivatePayload.builder()
						.id(user.getId())
						.email(user.getEmail())
						.username(user.getUsername())
						.role(user.getRole())
						.createdAt(user.getCreatedAt())
						.build())
				.orElseThrow(() -> new IgorNotFoundException(USER_NOT_FOUND));
	}

	@Override
	public User getUserByEmail(String email) {
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new IgorNotFoundException(USER_NOT_FOUND));
	}
}
