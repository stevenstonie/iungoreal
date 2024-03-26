package com.stevenst.app.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.stevenst.app.model.ChatroomParticipant;
import com.stevenst.app.model.ChatroomType;
import com.stevenst.app.repository.ChatroomRepository;
import com.stevenst.app.repository.UserRepository;
import com.stevenst.app.service.ChatService;
import com.stevenst.lib.exception.IgorNullValueException;
import com.stevenst.lib.exception.IgorUserNotFoundException;
import com.stevenst.lib.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
	private final ChatroomRepository chatroomRepository;
	private final UserRepository userRepository;
	private static final String USER_NOT_FOUND = "User not found";

	public List<String> getFriendsWithoutChatrooms(String username) {
		User user = getUserFromDbByUsername(username);

		// get all participants in chatrooms of type dm where the user is in
		List<ChatroomParticipant> friendsWithChatrooms = chatroomRepository
				.findAllByChatroomTypeAndUser(ChatroomType.DM, user);
		// get all friends
		// return all friends - friends that have chatrooms

		return friendsWithChatrooms.stream().map(ChatroomParticipant::getUser).map(User::getUsername).toList();
	}

	// --------------------------------------------------------

	private User getUserFromDbByUsername(String username) {
		if (username == null || username.equals("")) {
			throw new IgorNullValueException("Username cannot be null or empty.");
		}

		return userRepository.findByUsername(username)
				.orElseThrow(
						() -> new IgorUserNotFoundException(USER_NOT_FOUND + " (with username: " + username + ")."));
	}
}
