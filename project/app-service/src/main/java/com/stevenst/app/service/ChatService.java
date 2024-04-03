package com.stevenst.app.service;

import java.util.List;

import com.stevenst.app.model.ChatMessage;
import com.stevenst.app.payload.ChatroomPayload;
import com.stevenst.lib.payload.ResponsePayload;

public interface ChatService {
	public List<String> getFriendsWithoutDmChatrooms(String username);

	public ChatroomPayload createDmChatroom(String username, String friendUsername);

	public ChatroomPayload createGroupChatroom(String username);
	
	public List<ChatroomPayload> getAllDmChatroomsOfUser(String username);

	public ResponsePayload insertMessageIntoDb(ChatMessage chatMessage);

	public List<ChatMessage> getMessagesBeforeCursorByChatroomId(Long chatroomId, Long cursor, int limit);

	public ResponsePayload leaveChatroom(String username, Long chatroomId);

	public ResponsePayload updateChatroomName(Long chatroomId, String chatroomName);
}
