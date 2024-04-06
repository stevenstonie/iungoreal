package com.stevenst.app.service;

import java.util.List;

import com.stevenst.app.model.chat.ChatMessage;
import com.stevenst.app.payload.ChatroomPayload;
import com.stevenst.lib.payload.ResponsePayload;

public interface ChatService {
	public List<String> getFriendsWithoutDmChatrooms(String username);

	public List<String> getFriendsNotInChatroom(String username, Long chatroomId);

	public List<ChatroomPayload> getAllDmChatroomsOfUser(String username);

	public List<ChatroomPayload> getAllGroupChatroomsOfUser(String username);

	public List<ChatMessage> getMessagesBeforeCursorByChatroomId(Long chatroomId, Long cursor, int limit);

	public List<String> getAllMembersUsernamesInChatroom(Long chatroomId);

	public ResponsePayload insertMessageIntoDb(ChatMessage chatMessage);
	
	public ChatroomPayload createDmChatroom(String username, String friendUsername);
	
	public ChatroomPayload createGroupChatroom(String username);

	public ResponsePayload addUserToGroupChatroom(String username, Long chatroomId, String usernameOfUserToAdd);

	public ResponsePayload updateChatroomName(Long chatroomId, String chatroomName);
	
	public ResponsePayload leaveChatroom(String username, Long chatroomId);

	public ResponsePayload removeMemberFromChatroom(String username, Long chatroomId, String usernameOfMemberToRemove);
}
