package com.stevenst.app.controller;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.List;

import org.h2.tools.Server;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jayway.jsonpath.JsonPath;
import com.stevenst.app.model.FriendRequests;
import com.stevenst.app.model.Friendships;
import com.stevenst.app.payload.ResponsePayload;
import com.stevenst.app.repository.FriendRequestsRepository;
import com.stevenst.app.repository.FriendshipsRepository;
import com.stevenst.app.repository.UserRepository;
import com.stevenst.lib.model.User;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FriendsControllerIntegrationTest {
	private Server server;
	private static final User userAndrew = User.builder()
			.email("andrew_email123")
			.password("andrew_password123")
			.username("andrew_username123").build();
	private static final User userBobby = User.builder()
			.email("bobby_email123")
			.password("bobby_password123")
			.username("bobby_username123").build();

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private FriendshipsRepository friendshipsRepository;
	@Autowired
	private FriendRequestsRepository friendRequestsRepository;
	@Autowired
	private UserRepository userRepository;

	@BeforeAll
	void init() throws Exception {
		server = Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092");
		server.start();
	}

	@AfterAll
	void exit() throws Exception {
		server.stop();
	}

	@BeforeEach
	void setUp() throws Exception {
		insertUserIntoDB(userAndrew);
		insertUserIntoDB(userBobby);
	}

	@AfterEach
	void tearDown() throws SQLException {
		cleanDB();
	}

	@Test
	void sendFriendRequest() throws Exception {
		var result = mockMvc.perform(
				post("/api/friends/sendRequest?sender=" + userAndrew.getUsername() + "&receiver="
						+ userBobby.getUsername()))
				.andExpect(status().isOk())
				.andReturn();

		ResponsePayload response = getResponsePayloadFromMvcResult(result);

		assertEquals(200, response.getStatus());
		assertEquals("Friend request sent successfully (from " + userAndrew.getUsername() + " to "
				+ userBobby.getUsername() + ")", response.getMessage());
	}

	@Test
	void checkFriendRequest() throws Exception {
		addFriendRequest(userAndrew, userBobby);

		var result = mockMvc.perform(
				get("/api/friends/checkRequest?sender=" + userAndrew.getUsername() + "&receiver="
						+ userBobby.getUsername()))
				.andExpect(status().isOk())
				.andReturn();

		ResponsePayload response = getResponsePayloadFromMvcResult(result);

		assertEquals(200, response.getStatus());
		assertEquals("Friend request found (from " + userAndrew.getUsername() + " to "
				+ userBobby.getUsername() + ")", response.getMessage());
	}

	@Test
	void checkFriendship() throws Exception {
		addFriendship(userAndrew, userBobby);

		var result = mockMvc.perform(
				get("/api/friends/checkFriendship?user1=" + userAndrew.getUsername() + "&user2="
						+ userBobby.getUsername()))
				.andExpect(status().isOk())
				.andReturn();

		ResponsePayload response = getResponsePayloadFromMvcResult(result);

		assertEquals(200, response.getStatus());
		assertEquals("Friendship found (between " + userAndrew.getUsername() + " and "
				+ userBobby.getUsername() + ")", response.getMessage());
	}

	@Test
	void getAllFriendsUsernames() throws Exception {
		User userJoe = User.builder()
				.email("blob_joe_email123")
				.password("blob_joe_password123")
				.username("blob_joe_username123").build();
		insertUserIntoDB(userJoe);
		addFriendship(userAndrew, userJoe);
		addFriendship(userBobby, userJoe);

		var result = mockMvc.perform(
				get("/api/friends/getAllFriendsUsernames?username=" + userJoe.getUsername()))
				.andExpect(status().isOk())
				.andReturn();

		var responseJson = JsonPath.parse(result.getResponse().getContentAsString());
		int nbOfFriendsReturned = responseJson.read("$.length()");
		List<String> friendsUsernames = responseJson.read("$");

		assertEquals(2, nbOfFriendsReturned);
		assertTrue(friendsUsernames.contains(userBobby.getUsername()));
		assertTrue(friendsUsernames.contains(userAndrew.getUsername()));
	}

	@Test
	void acceptFriendRequest() throws Exception {
		addFriendRequest(userAndrew, userBobby);

		var result = mockMvc.perform(
				put("/api/friends/acceptRequest?sender=" + userAndrew.getUsername() + "&receiver="
						+ userBobby.getUsername()))
				.andExpect(status().isOk())
				.andReturn();

		ResponsePayload response = getResponsePayloadFromMvcResult(result);

		assertEquals(200, response.getStatus());
		assertEquals("Friend request accepted successfully (from " + userAndrew.getUsername() + " accepted by "
				+ userBobby.getUsername() + ")", response.getMessage());
	}

	@Test
	void cancelFriendRequest() throws Exception {
		addFriendRequest(userAndrew, userBobby);

		var result = mockMvc.perform(
				delete("/api/friends/cancelRequest?sender=" + userAndrew.getUsername() + "&receiver="
						+ userBobby.getUsername()))
				.andExpect(status().isOk())
				.andReturn();

		ResponsePayload response = getResponsePayloadFromMvcResult(result);

		assertEquals(200, response.getStatus());
		assertEquals("Friend request canceled successfully (from " + userAndrew.getUsername() + " to "
				+ userBobby.getUsername() + ")", response.getMessage());
	}

	@Test
	void declineFriendRequest() throws Exception {
		addFriendRequest(userAndrew, userBobby);

		var result = mockMvc.perform(
				delete("/api/friends/declineRequest?sender=" + userAndrew.getUsername() + "&receiver="
						+ userBobby.getUsername()))
				.andExpect(status().isOk())
				.andReturn();

		ResponsePayload response = getResponsePayloadFromMvcResult(result);

		assertEquals(200, response.getStatus());
		assertEquals("Friend request declined successfully (from " + userAndrew.getUsername() + " to "
				+ userBobby.getUsername() + ")", response.getMessage());
	}

	@Test
	void unfriend() throws Exception {
		addFriendship(userAndrew, userBobby);

		var result = mockMvc.perform(
				delete("/api/friends/unfriend?unfriender=" + userAndrew.getUsername() + "&unfriended="
						+ userBobby.getUsername()))
				.andExpect(status().isOk())
				.andReturn();

		ResponsePayload response = getResponsePayloadFromMvcResult(result);

		assertEquals(200, response.getStatus());
		assertEquals("Unfriend successfully done (" + userAndrew.getUsername() + " unfriended "
				+ userBobby.getUsername() + ")", response.getMessage());
	}

	@Test
	void sendRequest_alreadyReceivingOne() throws Exception {
		addFriendRequest(userBobby, userAndrew);

		var result = mockMvc.perform(
				post("/api/friends/sendRequest?sender=" + userAndrew.getUsername() + "&receiver="
						+ userBobby.getUsername()))
				.andExpect(status().isBadRequest())
				.andReturn();

		ResponsePayload response = getResponsePayloadFromMvcResult(result);

		assertEquals(400, response.getStatus());
		assertEquals("Cannot send a friend request when having one already received (from " + userBobby.getUsername()
				+ " to " + userAndrew.getUsername() + ")", response.getMessage());
	}

	// -------------------------------------------------

	private void insertUserIntoDB(User user) {
		userRepository.save(user);
	}

	private void addFriendRequest(User sender, User receiver) {
		friendRequestsRepository.save(FriendRequests.builder()
				.sender(sender)
				.receiver(receiver)
				.build());
	}

	private void addFriendship(User user1, User user2) {
		friendshipsRepository.save(Friendships.builder()
				.user1(user1)
				.user2(user2)
				.build());
	}

	private void cleanDB() {
		friendRequestsRepository.deleteAll();
		friendshipsRepository.deleteAll();
		userRepository.deleteAll();
	}

	ResponsePayload getResponsePayloadFromMvcResult(MvcResult result) throws UnsupportedEncodingException {
		var responseJson = JsonPath.parse(result.getResponse().getContentAsString());
		String message = responseJson.read("$.message");
		int status = responseJson.read("$.status");
		return ResponsePayload.builder()
				.message(message)
				.status(status)
				.build();
	}
}