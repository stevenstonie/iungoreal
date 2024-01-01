package com.stevenst.app.service;

import com.stevenst.app.payload.UserPrivatePayload;
import com.stevenst.app.payload.UserPublicPayload;

public interface UserService {
	Long getIdByUsername(String username);

	UserPublicPayload getUserPublicByUsername(String username);

	UserPrivatePayload getUserPrivateByUsername(String username);

	UserPrivatePayload getUserByEmail(String email);
}
