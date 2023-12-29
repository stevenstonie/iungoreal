package com.stevenst.app.service.impl;

import org.springframework.stereotype.Service;

import com.stevenst.app.exception.IgorNotFoundException;
import com.stevenst.app.repository.UserRepository;
import com.stevenst.app.service.UserService;
import com.stevenst.lib.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;

	@Override
	public User getUserByUsername(String username) {
		return userRepository.findByUsername(username)
				.orElseThrow(() -> new IgorNotFoundException("User not found"));
	}

	@Override
	public User getUserByEmail(String email) {
		return userRepository.findByEmail(email)
			.orElseThrow( () -> new IgorNotFoundException("User not found"));
	}
}
