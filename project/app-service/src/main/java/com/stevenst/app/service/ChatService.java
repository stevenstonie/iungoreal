package com.stevenst.app.service;

import java.util.List;

public interface ChatService {
	public List<String> getFriendsWithoutChatrooms(String username);
}
