package com.stevenst.app.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.stevenst.app.payload.UserPublicPayload;
import com.stevenst.app.repository.UserRepository;
import com.stevenst.app.service.SearchService;
import com.stevenst.lib.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {
	private final UserRepository userRepository;

	@Override
	public List<UserPublicPayload> getUsersMatching(String input) {
		if (input == null || input.isEmpty()) {
			throw new IllegalArgumentException("input cannot be null or empty.");
		}
		if (input.length() < 3) {
			throw new IllegalArgumentException("input must be at least 3 characters long.");
		}

		List<User> matchingUsers = userRepository.findUsersByUsernameContaining(input);

		return matchingUsers.stream()
				.map(user -> new UserPublicPayload(user.getId(), user.getUsername(), user.getProfilePictureName()))
				.collect(Collectors.toList());
	}
}
