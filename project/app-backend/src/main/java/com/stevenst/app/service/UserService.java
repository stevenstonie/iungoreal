package com.stevenst.app.service;

import org.springframework.stereotype.Service;

import com.stevenst.app.model.User;

@Service
public interface UserService {
	User getCurrentUserByToken(String authHeader);
}
