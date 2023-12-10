package com.stevenst.app.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.stevenst.app.model.User;

@Service
public interface UserService {
	Optional<User> getUserByEmail(String email);
}
