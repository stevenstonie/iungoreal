package com.stevenst.app.controller;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jayway.jsonpath.JsonPath;
import com.stevenst.app.model.FriendRequests;
import com.stevenst.app.payload.MessagePayload;
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
	private static final User testSender = User.builder()
			.email("test_sender_email123")
			.password("test_sender_password123")
			.username("test_sender_username123").build();
	private static final User testReceiver = User.builder()
			.email("test_receiver_email123")
			.password("test_receiver_password123")
			.username("test_receiver_username123").build();

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
		insertUserIntoDB(testSender);
		insertUserIntoDB(testReceiver);
	}

	@AfterEach
	void tearDown() throws SQLException {
		cleanDB();
	}

	@Test
	void sendFriendRequest() throws Exception {
		var result = mockMvc.perform(
				post("/api/friends/sendRequest?sender=" + testSender.getUsername() + "&receiver="
						+ testReceiver.getUsername()))
				.andExpect(status().isOk())
				.andReturn();

		MessagePayload response = getMessagePayloadFromMvcResult(result);

		assertTrue(response.isSuccess());
		assertEquals("Friend request sent successfully (from " + testSender.getUsername() + " to "
				+ testReceiver.getUsername() + ")", response.getMessage());
	}

	@Test
	void checkFriendRequest() throws Exception {
		addFriendRequest(testSender, testReceiver);

		var result = mockMvc.perform(
				get("/api/friends/checkRequest?sender=" + testSender.getUsername() + "&receiver="
						+ testReceiver.getUsername()))
				.andExpect(status().isOk())
				.andReturn();

		MessagePayload response = getMessagePayloadFromMvcResult(result);

		assertTrue(response.isSuccess());
		assertEquals("Friend request found (from " + testSender.getUsername() + " to "
				+ testReceiver.getUsername() + ")", response.getMessage());
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
	
	private void cleanDB() {
		friendRequestsRepository.deleteAll();
		friendshipsRepository.deleteAll();
		userRepository.deleteAll();
	}

	MessagePayload getMessagePayloadFromMvcResult(MvcResult result) throws UnsupportedEncodingException {
		var responseJson = JsonPath.parse(result.getResponse().getContentAsString());
		String message = responseJson.read("$.message");
		boolean success = responseJson.read("$.success");
		return MessagePayload.builder()
				.message(message)
				.success(success)
				.build();
	}
}