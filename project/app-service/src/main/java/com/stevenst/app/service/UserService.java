package com.stevenst.app.service;

import com.stevenst.app.payload.UserPrivatePayload;
import com.stevenst.app.payload.UserPublicPayload;
import com.stevenst.lib.model.User;

public interface UserService {
	public UserPublicPayload getUserPublicByUsername(String username);

	public UserPrivatePayload getUserPrivateByUsername(String username);

	public User getUserByEmail(String email);
}
