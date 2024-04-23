package com.stevenst.app.service;

import java.util.List;

import com.stevenst.app.payload.UserPublicPayload;

public interface SearchService {
	public List<UserPublicPayload> getUsersMatching(String input);
}
