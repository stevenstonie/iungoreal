package com.stevenst.app.service;

import com.stevenst.lib.model.User;

public interface UserService {
	public User getUserByUsername(String username);

	public User getUserByEmail(String email);
}
