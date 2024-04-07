package com.stevenst.app.service.impl;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.stevenst.app.model.chat.ChatMessage;
import com.stevenst.app.model.chat.Chatroom;
import com.stevenst.app.model.chat.ChatroomParticipant;
import com.stevenst.app.model.chat.ChatroomType;
import com.stevenst.app.payload.ChatroomPayload;
import com.stevenst.app.repository.ChatMessageRepository;
import com.stevenst.app.repository.ChatroomParticipantRepository;
import com.stevenst.app.repository.ChatroomRepository;
import com.stevenst.app.repository.UserRepository;
import com.stevenst.app.service.ChatService;
import com.stevenst.lib.exception.IgorEntityAlreadyExistsException;
import com.stevenst.lib.exception.IgorEntityNotFoundException;
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

	@Override
	public List<ChatMessage> getMessagesBeforeCursorByChatroomId(Long chatroomId, Long cursor, int limit) {
		PageRequest pageRequest = PageRequest.of(0, limit, Sort.by("id").descending());

		return chatMessageRepository.findMessagesBeforeCursorByChatroomId(chatroomId, cursor, pageRequest);
	}

	@Override
	public List<String> getFriendsWithoutDmChatrooms(String username) {
		User user = getUserFromDbByUsername(username);

		// get all participants in chatrooms of type dm where the user is in
		List<ChatroomParticipant> friendsWithDmChatrooms = getParticipantsInDmsOfUser(user);

		// get all friends of user
		List<String> allFriendsUsernames = getAllFriendsUsernamesOfUser(username);

		// return the friend list filtering out those who have dm chatrooms
		return allFriendsUsernames.stream().filter(friend -> !friendsWithDmChatrooms.stream()
				.anyMatch(participant -> participant.getUser().getUsername().equals(friend)))
				.toList();
	}

	@Override
	public List<String> getFriendsNotInChatroom(String username, Long chatroomId) {
		User user = getUserFromDbByUsername(username);
		Chatroom chatroom = chatroomRepository.findById(chatroomId).get();

		// get all friends usernames of user
		List<String> allFriendsUsernames = getAllFriendsUsernamesOfUser(username);

		// get all participants in chatroom
		List<ChatroomParticipant> chatroomParticipants = chatroomParticipantRepository.findByChatroom(chatroom);

		// return the friend list filtering out those who are in the chatroom
		return allFriendsUsernames.stream().filter(friend -> !chatroomParticipants.stream()
				.anyMatch(participant -> participant.getUser().getUsername().equals(friend)))
				.toList();
	}

	@Override
	public List<ChatroomPayload> getAllDmChatroomsOfUser(String username) {
		User user = getUserFromDbByUsername(username);

		return getAllDmChatroomsOfUser(user);
	}

	@Override
	public List<ChatroomPayload> getAllGroupChatroomsOfUser(String username) {
		User user = getUserFromDbByUsername(username);

		return getAllGroupChatroomsOfUser(user);
	}

	@Override
	public List<String> getAllMembersUsernamesInChatroom(Long chatroomId) {
		Chatroom chatroom = chatroomRepository.findById(chatroomId).get();

		List<ChatroomParticipant> chatroomParticipants = chatroomParticipantRepository.findByChatroom(chatroom);

		return chatroomParticipants.stream().map(participant -> participant.getUser().getUsername()).toList();
	}

	@Override
	public ResponsePayload insertMessageIntoDb(ChatMessage chatMessage) {
		chatMessageRepository.save(chatMessage);

		return ResponsePayload.builder().status(201).message("Message inserted successfully.").build();
	}

	@Override
	public ChatroomPayload createDmChatroom(String username, String friendUsername) {
		User user = getUserFromDbByUsername(username);
		User friend = getUserFromDbByUsername(friendUsername);

		// check if these two users are already sharing a dm chatroom
		Chatroom commonDmChatroom = chatroomParticipantRepository.findCommonDmChatroomOfUsers(user, friend);

		// if yes then set the user participant's 'hasLeft' to false
		if (commonDmChatroom != null) {
			ChatroomParticipant participant = chatroomParticipantRepository
					.findByUserAndChatroom(user, commonDmChatroom);
			participant.setHasLeft(false);
			chatroomParticipantRepository.save(participant);

			// only return the friend as participant to use participants[0] when displaying the chatroom
			return createChatroomPayload(commonDmChatroom, List.of(friendUsername));
		}

		// else create a new chatroom and add participants
		Chatroom chatroom = chatroomRepository
				.save(Chatroom.builder().name(user.getUsername() + " and " + friend.getUsername() + "'s chatroom")
						.type(ChatroomType.DM).build());

		chatroomParticipantRepository.save(
				ChatroomParticipant.builder().user(user).chatroom(chatroom).hasLeft(false).build());
		chatroomParticipantRepository.save(
				ChatroomParticipant.builder().user(friend).chatroom(chatroom).hasLeft(false).build());

		// same as the return above
		return createChatroomPayload(chatroom, List.of(friend.getUsername()));
	}

	@Override
	public ChatroomPayload createGroupChatroom(String username) {
		User user = getUserFromDbByUsername(username);

		Chatroom chatroom = chatroomRepository
				.save(Chatroom.builder().name(user.getUsername() + "'s group chatroom").type(ChatroomType.GROUP)
						.adminUsername(user.getUsername()).build());

		chatroomParticipantRepository.save(
				ChatroomParticipant.builder().user(user).chatroom(chatroom).hasLeft(false).build());

		return createChatroomPayload(chatroom, List.of(user.getUsername()));
	}

	@Override
	public ResponsePayload addUserToGroupChatroom(String username, Long chatroomid, String usernameOfUserToAdd) {
		User user = getUserFromDbByUsername(username);
		Chatroom chatroom = chatroomRepository.findById(chatroomid).get();
		User userToAdd = getUserFromDbByUsername(usernameOfUserToAdd);

		if (!user.getUsername().equals(chatroom.getAdminUsername())) {
			throw new RuntimeException(
					"User " + username + " is not the admin of chatroom with id " + chatroomid + "."); // TODO: add a custom IgorUnauthorizedOperationException
		}

		ChatroomParticipant userParticipant = chatroomParticipantRepository.findByUserAndChatroom(userToAdd, chatroom);

		if (userParticipant != null) {
			throw new IgorEntityAlreadyExistsException(
					"User " + usernameOfUserToAdd + " is already in chatroom with id " + chatroomid + ".");
		}

		chatroomParticipantRepository
				.save(ChatroomParticipant.builder().user(userToAdd).chatroom(chatroom).hasLeft(false).build());

		return ResponsePayload.builder().status(201)
				.message("User " + usernameOfUserToAdd + " added to chatroom successfully.").build();
	}

	@Override
	public ResponsePayload updateChatroomName(Long chatroomId, String chatroomName) {
		Chatroom chatroom = chatroomRepository.findById(chatroomId).get();
		if (chatroom == null) {
			throw new IgorEntityNotFoundException("Chatroom with id " + chatroomId + " not found.");
		}

		chatroom.setName(chatroomName);
		chatroomRepository.save(chatroom);

		return ResponsePayload.builder().status(200).message("Chatroom name updated successfully.").build();
	}

	@Override
	public ResponsePayload leaveChatroom(String username, Long chatroomId) {
		User user = getUserFromDbByUsername(username);
		Chatroom chatroom = chatroomRepository.findById(chatroomId).get();
		ChatroomParticipant participant = chatroomParticipantRepository.findByUserAndChatroom(user, chatroom);

		if (chatroom.getType() == ChatroomType.DM) {
			// for dms -> when users leave mark them as left
			participant.setHasLeft(true);
			chatroomParticipantRepository.save(participant);

			// for dms -> if both users have left then remove both as participants and the chatroom as well
			Long count = chatroomParticipantRepository.countByChatroomAndHasLeftIsFalse(chatroom);
			if (count == 0) {
				removeChatroomAndParticipantsAndMessages(chatroom);

				return ResponsePayload.builder().status(200)
						.message("User " + username + " left the chatroom and chatroom removed successfully.").build();
			} else {
				return ResponsePayload.builder().status(200).message("User " + username + " left the chatroom.")
						.build();
			}
		}

		// for groups -> when users leave then remove them as participants
		chatroomParticipantRepository.delete(participant);

		// for groups -> if the group is empty then remove the group and messages as well
		Long count = chatroomParticipantRepository.countByChatroomAndHasLeftIsFalse(chatroom);
		if (count == 0) {
			removeChatroomAndParticipantsAndMessages(chatroom);

			return ResponsePayload.builder().status(200)
					.message("User " + username + " left the chatroom and chatroom removed successfully.").build();
		} else {
			// if the user was an admin assign the oldest member as new admin
			if (chatroom.getAdminUsername().equals(username)) {
				chatroom.setAdminUsername(getUsernameOfOldestParticipant(chatroom));
				chatroomRepository.save(chatroom);
			}

			return ResponsePayload.builder().status(200).message("User " + username + " left the chatroom.")
					.build();
		}
	}

	@Override
	public ResponsePayload removeMemberFromChatroom(String username, Long chatroomId, String usernameOfMemberToRemove) {
		User user = getUserFromDbByUsername(username);
		Chatroom chatroom = chatroomRepository.findById(chatroomId).get();
		User userToRemove = getUserFromDbByUsername(usernameOfMemberToRemove);

		if (!user.getUsername().equals(chatroom.getAdminUsername())) {
			throw new RuntimeException(
					"User " + username + " is not the admin of chatroom with id " + chatroomId + "."); // TODO: add a custom IgorUnauthorizedOperationException
		}
		if (username.equals(usernameOfMemberToRemove)) {
			throw new RuntimeException("Cannot remove self from chatroom.");
		}

		ChatroomParticipant participant = chatroomParticipantRepository
				.findByUserAndChatroom(userToRemove, chatroom);
		if (participant == null) {
			throw new IgorEntityNotFoundException(
					"Cannot remove user " + usernameOfMemberToRemove + " if the user is not in the chatroom with id "
							+ chatroomId + ".");
		}
		chatroomParticipantRepository.delete(participant);

		return ResponsePayload.builder().status(200)
				.message("User " + usernameOfMemberToRemove + " removed from chatroom with id " + chatroomId
						+ " successfully.")
				.build();
	}

	// --------------------------------------------------------

	private String getUsernameOfOldestParticipant(Chatroom chatroom) {
		ChatroomParticipant oldestParticipant = chatroomParticipantRepository
				.findParticipantsInChatroomFromOldest(chatroom.getId(), PageRequest.of(0, 1)).get(0);

		return oldestParticipant.getUser().getUsername();
	}

	private List<String> getAllFriendsUsernamesOfUser(String username) {
		// get all friends of user
		Mono<List<String>> friendsUsernamesMono = webClient.get()
				.uri(uriBuilder -> uriBuilder.path("/api/friend/getAllFriendsUsernames")
						.queryParam("username", username)
						.build())
				.retrieve()
				.bodyToMono(new ParameterizedTypeReference<List<String>>() {
				});

		// Convert the Mono to a List and return it
		return friendsUsernamesMono.block();
	}

	// TODO: refactor the code: separate the dm chatrooms code from the group and regional ones

	private ChatroomPayload createChatroomPayload(Chatroom chatroom, List<String> participantsUsernames) {
		return ChatroomPayload.builder()
				.id(chatroom.getId())
				.name(chatroom.getName())
				.type(chatroom.getType())
				.adminUsername(chatroom.getAdminUsername())
				.lastMessageTime(chatroom.getLastMessageTime())
				.participantsUsernames(participantsUsernames)
				.build();
	}

	private void removeChatroomAndParticipantsAndMessages(Chatroom chatroom) {
		chatMessageRepository.deleteAllByChatroomId(chatroom.getId());
		chatroomParticipantRepository.deleteByChatroom(chatroom);
		chatroomRepository.delete(chatroom);
	}

	private List<ChatroomPayload> getAllDmChatroomsOfUser(User user) {
		// get all dms of user where user hasLeft = false
		List<Chatroom> dmChatroomsOfUser = chatroomParticipantRepository.findDmChatroomsOfUserNotLeft(user);

		// get all participants (excluding the user) that have a chatroom in common with the user
		List<ChatroomParticipant> participantsInDms = getParticipantsInDmsOfUser(user);

		List<ChatroomPayload> chatrooms = new java.util.ArrayList<>();

		for (Chatroom chatroomOfUser : dmChatroomsOfUser) {
			ChatroomPayload payload = ChatroomPayload.builder()
					.id(chatroomOfUser.getId())
					.name(chatroomOfUser.getName())
					.type(chatroomOfUser.getType())
					.lastMessageTime(chatroomOfUser.getLastMessageTime())
					.participantsUsernames(participantsInDms.stream()
							.filter(participant -> participant.getChatroom().getId().equals(chatroomOfUser.getId()))
							.map(participant -> participant.getUser().getUsername())
							.toList())
					.build();
			chatrooms.add(payload);
		}

		return chatrooms;
	}

	private List<ChatroomPayload> getAllGroupChatroomsOfUser(User user) {
		List<Chatroom> groupChatroomsOfUser = chatroomParticipantRepository.findGroupChatroomsOfUser(user);

		List<ChatroomPayload> chatrooms = new java.util.ArrayList<>();

		for (Chatroom groupChatroomOfUser : groupChatroomsOfUser) {
			// for each chatroom search for its participants and insert it into the payload
			List<String> participantsUsernames = chatroomParticipantRepository.findByChatroom(groupChatroomOfUser)
					.stream().map(participant -> participant.getUser().getUsername()).toList();

			ChatroomPayload payload = ChatroomPayload.builder()
					.id(groupChatroomOfUser.getId())
					.name(groupChatroomOfUser.getName())
					.type(groupChatroomOfUser.getType())
					.adminUsername(groupChatroomOfUser.getAdminUsername())
					.lastMessageTime(groupChatroomOfUser.getLastMessageTime())
					.participantsUsernames(participantsUsernames)
					.build();
			chatrooms.add(payload);
		}

		return chatrooms;
	}

	private List<ChatroomParticipant> getParticipantsInDmsOfUser(User user) {
		// get all chatrooms of user where he hasnt left (is set to false)
		List<Chatroom> dmsOfUserNotLeft = chatroomParticipantRepository.findDmChatroomsOfUserNotLeft(user);

		// get all participants (excluding the user) that exist in those selected chatrooms
		return chatroomParticipantRepository.findParticipantsInTheseChatroomsExcludingUser(dmsOfUserNotLeft, user);
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
