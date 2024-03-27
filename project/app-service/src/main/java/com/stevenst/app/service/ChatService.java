package com.stevenst.app.service;

import java.util.List;

import com.stevenst.app.payload.ChatroomPayload;
import com.stevenst.lib.payload.ResponsePayload;

public interface ChatService {
	public List<String> getFriendsWithoutDmChatrooms(String username);

	public ResponsePayload createChatroom(String username, String friendUsername);

	public List<ChatroomPayload> getAllChatroomsOfUser(String username);
}
