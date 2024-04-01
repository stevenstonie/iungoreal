package com.stevenst.app.service.impl;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.stevenst.app.model.ChatMessage;
import com.stevenst.app.model.Chatroom;
import com.stevenst.app.model.ChatroomParticipant;
import com.stevenst.app.model.ChatroomType;
import com.stevenst.app.payload.ChatroomPayload;
import com.stevenst.app.repository.ChatMessageRepository;
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
	private final ChatMessageRepository chatMessageRepository;
	private final UserRepository userRepository;
	private final WebClient webClient;
	private static final String USER_NOT_FOUND = "User not found";

	public List<String> getFriendsWithoutDmChatrooms(String username) {
		User user = getUserFromDbByUsername(username);

		// get all participants in chatrooms of type dm where the user is in
		List<ChatroomParticipant> friendsWithDmChatrooms = getParticipantsWithCommonChatrooms(user,
				List.of(ChatroomType.DM));

		// get all friends of user
		Mono<List<String>> friendsMono = webClient.get()
				.uri(uriBuilder -> uriBuilder.path("/api/friend/getAllFriendsUsernames")
						.queryParam("username", username)
						.build())
				.retrieve()
				.bodyToMono(new ParameterizedTypeReference<List<String>>() {
				});

		// Convert the Mono to a List
		List<String> allFriends = friendsMono.block();

		// return the friend list filtering out those who have dm chatrooms
		return allFriends.stream().filter(friend -> !friendsWithDmChatrooms.stream()
				.anyMatch(participant -> participant.getUser().getUsername().equals(friend)))
				.toList();
	}

	@Override
	public ResponsePayload createChatroom(String username, String friendUsername) {
		User user = getUserFromDbByUsername(username);
		User friend = getUserFromDbByUsername(friendUsername);

		Chatroom chatroom = chatroomRepository
				.save(Chatroom.builder().name(user.getUsername() + " and " + friend.getUsername() + "'s chatroom")
						.type(ChatroomType.DM).build());

		chatroomParticipantRepository.save(
				ChatroomParticipant.builder().user(user).chatroom(chatroom).build());
		chatroomParticipantRepository.save(
				ChatroomParticipant.builder().user(friend).chatroom(chatroom).build());

		return ResponsePayload.builder().status(201)
				.message("Chatroom created for " + username + " and " + friendUsername + ".").build();
	}

	public List<ChatroomPayload> getAllDmChatroomsOfUser(String username) {
		User user = getUserFromDbByUsername(username);

		List<ChatroomPayload> dmChatrooms = returnChatroomOfUserByType(user, ChatroomType.DM);

		for (int i = 0; i < dmChatrooms.size(); i++) {
			dmChatrooms.get(i).setParticipantsUsernames(dmChatrooms.get(i)
					.getParticipantsUsernames().stream()
					.filter(participant -> !participant.equals(user.getUsername()))
					.toList());
		}

		return dmChatrooms;
	}

	public ResponsePayload insertMessageIntoDb(ChatMessage chatMessage) {
		chatMessageRepository.save(chatMessage);

		return ResponsePayload.builder().status(201).message("Message inserted successfully.").build();
	}

	public List<ChatMessage> getMessagesBeforeCursorByChatroomId(Long chatroomId, Long cursor, int limit) {
		PageRequest pageRequest = PageRequest.of(0, limit, Sort.by("id").descending());

		return chatMessageRepository.findMessagesBeforeCursorByChatroomId(chatroomId, cursor, pageRequest);
	}

	@Override
	public ResponsePayload leaveChatroom(String username, Long chatroomId) {
		// for dms -> when users leave mark them as left
		// for groups -> when users leave then remove them as participants
		User user = getUserFromDbByUsername(username);
		Chatroom chatroom = chatroomRepository.findById(chatroomId).get();

		if (chatroom.getType() == ChatroomType.DM) {
			ChatroomParticipant participant = chatroomParticipantRepository.findByUserAndChatroom(user, chatroom);
			participant.setHasLeft(true);
			chatroomParticipantRepository.save(participant);
		} else {
			// TODO
		}

		// for dms -> if both users have left then remove both as participants and the chatroom as well
		// for groups -> if the group is empty then remove the group as well
		Long count = chatroomParticipantRepository.countByChatroomAndHasLeftIsFalse(chatroom);
		if (count == 0) {
			chatroomParticipantRepository.deleteByChatroom(chatroom);
			chatroomRepository.delete(chatroom);

			return ResponsePayload.builder().status(200)
					.message("User " + username + " left the chatroom and chatroom removed successfully.").build();
		} else {
			return ResponsePayload.builder().status(200).message("User " + username + " left the chatroom.").build();
		}
	}

	// --------------------------------------------------------

	private List<ChatroomPayload> returnChatroomOfUserByType(User user, ChatroomType type) {
		List<Chatroom> dmChatroomsOfUser = chatroomParticipantRepository.findChatroomsOfUserAndType(user,
				type);

		List<ChatroomParticipant> participantsInCommonChatrooms = getParticipantsWithCommonChatrooms(user,
				List.of(type));

		List<ChatroomPayload> chatrooms = new java.util.ArrayList<>();

		for (Chatroom chatroomOfUser : dmChatroomsOfUser) {
			ChatroomPayload payload = ChatroomPayload.builder()
					.id(chatroomOfUser.getId())
					.name(chatroomOfUser.getName())
					.type(chatroomOfUser.getType())
					.lastMessageTime(chatroomOfUser.getLastMessageTime())
					.participantsUsernames(participantsInCommonChatrooms.stream()
							.filter(participant -> participant.getChatroom().getId().equals(chatroomOfUser.getId()))
							.map(participant -> participant.getUser().getUsername())
							.toList())
					.build();
			chatrooms.add(payload);
		}

		return chatrooms;
	}

	private List<ChatroomParticipant> getParticipantsWithCommonChatrooms(User user, List<ChatroomType> types) {
		return chatroomRepository
				.findParticipantsWithCommonChatrooms(user, types);
	}
	// TODO: look into this

	private User getUserFromDbByUsername(String username) {
		if (username == null || username.equals("")) {
			throw new IgorNullValueException("Username cannot be null or empty.");
		}

		return userRepository.findByUsername(username)
				.orElseThrow(
						() -> new IgorUserNotFoundException(USER_NOT_FOUND + " (with username: " + username + ")."));
	}
}
