package com.stevenst.app.service.impl;

import org.springframework.stereotype.Service;

import com.stevenst.app.exception.IgorAuthenticationException;
import com.stevenst.app.model.User;
import com.stevenst.app.repository.UserRepository;
import com.stevenst.app.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private final UserRepository userRepository;
	private final JwtServiceImpl jwtService;

	@Override
	public User getCurrentUserByToken(String authHeader) {
		String token = jwtService.extractToken(authHeader);
		String email = jwtService.extractEmail(token);
		var user = userRepository.findByEmail(email);

		if (!user.isPresent()) {
			throw new IgorAuthenticationException("User not found");
		}

		return user.get();
	}

}
