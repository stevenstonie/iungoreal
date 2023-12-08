package com.stevenst.app.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.stevenst.app.model.User;
import com.stevenst.app.repository.UserRepository;
import com.stevenst.app.service.UserService;

@Service
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;
	
	public UserServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	@Override
	public Optional<User> getUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}
	
}
