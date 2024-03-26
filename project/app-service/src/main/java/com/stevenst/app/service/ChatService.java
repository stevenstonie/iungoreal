package com.stevenst.app.service;

import java.util.List;

import com.stevenst.lib.payload.ResponsePayload;

public interface ChatService {
	public List<String> getFriendsWithoutChatrooms(String username);

	public ResponsePayload createChatroom(String username, String friendUsername);

	public List<String> getDmChatroomsOfFriends(String username);
}
