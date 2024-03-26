package com.stevenst.app.service.impl;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.stevenst.app.model.Chatroom;
import com.stevenst.app.model.ChatroomParticipant;
import com.stevenst.app.model.ChatroomType;
import com.stevenst.app.repository.ChatroomParticipantRepository;
import com.stevenst.app.repository.ChatroomRepository;
import com.stevenst.app.repository.UserRepository;
import com.stevenst.app.service.ChatService;
import com.stevenst.lib.exception.IgorNullValueException;
import com.stevenst.lib.exception.IgorUserNotFoundException;
import com.stevenst.lib.model.User;
import com.stevenst.lib.payload.ResponsePayload;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
	private final ChatroomRepository chatroomRepository;
	private final ChatroomParticipantRepository chatroomParticipantRepository;
	private final UserRepository userRepository;
	private final WebClient webClient;
	private static final String USER_NOT_FOUND = "User not found";

	public List<String> getFriendsWithoutChatrooms(String username) {
		User user = getUserFromDbByUsername(username);

		// get all participants in chatrooms of type dm where the user is in
		List<ChatroomParticipant> friendsWithChatrooms = getFriendsUsernamesWithChatrooms(user);

		// get all friends
		Mono<List<String>> friendsMono = webClient.get()
				.uri(uriBuilder -> uriBuilder.path("/api/friend/getAllFriendsUsernames")
						.queryParam("username", username)
						.build())
				.retrieve()
				.bodyToMono(new ParameterizedTypeReference<List<String>>() {
				});

		// Convert the Mono to a List
		List<String> allFriends = friendsMono.block();

		// return all friends - friends that have chatrooms
		return allFriends.stream().filter(friend -> !friendsWithChatrooms.stream()
				.anyMatch(participant -> participant.getUser().getUsername().equals(friend)))
				.toList();
	}

	@Override
	public ResponsePayload createChatroom(String username, String friendUsername) {
		User user = getUserFromDbByUsername(username);
		User friend = getUserFromDbByUsername(friendUsername);

		// create chatroom 
		Chatroom chatroom = chatroomRepository
				.save(Chatroom.builder().name(user.getUsername() + " and " + friend.getUsername() + "'s chatroom")
						.type(ChatroomType.DM).build());

		// add these two as participants
		chatroomParticipantRepository.save(
				ChatroomParticipant.builder().user(user).chatroom(chatroom).build());
		chatroomParticipantRepository.save(
				ChatroomParticipant.builder().user(friend).chatroom(chatroom).build());

		return ResponsePayload.builder().status(201)
				.message("Chatroom created for " + username + " and " + friendUsername + ".").build();
	}

	public List<String> getDmChatroomsOfFriends(String username) {
		User user = getUserFromDbByUsername(username);

		List<ChatroomParticipant> friendsWithChatrooms = getFriendsUsernamesWithChatrooms(user);
		System.out.println(friendsWithChatrooms.stream()
				.map(participant -> participant.getUser().getUsername())
				.toList());

		// get friends in chatrooms that the user is in
		return friendsWithChatrooms.stream()
				.filter(participant -> !participant.getUser().getUsername().equals(username))
				.map(participant -> participant.getUser().getUsername())
				.toList();
	}

	// --------------------------------------------------------

	private List<ChatroomParticipant> getFriendsUsernamesWithChatrooms(User user) {
		return chatroomRepository
				.findParticipantsWithCommonChatrooms(ChatroomType.DM, user);
	}

	private User getUserFromDbByUsername(String username) {
		if (username == null || username.equals("")) {
			throw new IgorNullValueException("Username cannot be null or empty.");
		}

		return userRepository.findByUsername(username)
				.orElseThrow(
						() -> new IgorUserNotFoundException(USER_NOT_FOUND + " (with username: " + username + ")."));
	}
}
