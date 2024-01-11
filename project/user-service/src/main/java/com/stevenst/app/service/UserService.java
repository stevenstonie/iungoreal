package com.stevenst.app.service;

import org.springframework.web.multipart.MultipartFile;

import com.stevenst.app.payload.UserPrivatePayload;
import com.stevenst.app.payload.UserPublicPayload;
import com.stevenst.lib.payload.ResponsePayload;

public interface UserService {
	UserPublicPayload getUserPublicByUsername(String username);

	UserPrivatePayload getUserPrivateByUsername(String username);

	UserPrivatePayload getUserByEmail(String email);

	ResponsePayload saveProfilePicture(String username, MultipartFile file);
}
