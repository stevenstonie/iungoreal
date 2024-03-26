package com.stevenst.app.service.impl;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.stevenst.app.model.ChatroomParticipant;
import com.stevenst.app.model.ChatroomType;
import com.stevenst.app.repository.ChatroomRepository;
import com.stevenst.app.repository.UserRepository;
import com.stevenst.app.service.ChatService;
import com.stevenst.lib.exception.IgorNullValueException;
import com.stevenst.lib.exception.IgorUserNotFoundException;
import com.stevenst.lib.model.User;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
	private final ChatroomRepository chatroomRepository;
	private final UserRepository userRepository;
	private final WebClient webClient;
	private static final String USER_NOT_FOUND = "User not found";

	public List<String> getFriendsWithoutChatrooms(String username) {
		User user = getUserFromDbByUsername(username);

		// get all participants in chatrooms of type dm where the user is in
		List<ChatroomParticipant> friendsWithChatrooms = chatroomRepository
				.findAllByChatroomTypeAndUser(ChatroomType.DM, user);

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
