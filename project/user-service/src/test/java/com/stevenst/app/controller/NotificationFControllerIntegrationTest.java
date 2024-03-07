package com.stevenst.app.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.stevenst.app.model.FriendRequests;
import com.stevenst.app.model.Friendships;
import com.stevenst.app.payload.NotificationFPayload;
import com.stevenst.app.repository.NotificationRepository;
import com.stevenst.app.repository.UserRepository;
import com.stevenst.lib.model.Notification;
import com.stevenst.lib.model.User;
import com.stevenst.lib.model.enums.NotificationType;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NotificationFControllerIntegrationTest {
	private Server server;
	private static final User userAndrew = User.builder()
			.email("andrew_email123")
			.password("andrew_password123")
			.username("andrew_username123").build();
	private static final User userBobby = User.builder()
			.email("bobby_email123")
			.password("bobby_password123")
			.username("bobby_username123").build();
	private static final User userDoug = User.builder()
			.email("doug_email123")
			.password("doug_password123")
			.username("doug_username123").build();

	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private NotificationRepository notificationRepository;
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
		insertUserIntoDB(userDoug);

		insertNotificationFIntoDB(userAndrew, userBobby, NotificationType.FRIEND_REQUEST,
				userBobby.getUsername() + " sent you a friend request.");
		insertNotificationFIntoDB(userAndrew, userDoug, NotificationType.FRIEND_REQUEST_ACCEPTED,
				userDoug.getUsername() + " accepted your friend request.");
		insertNotificationFIntoDB(userBobby, userDoug, NotificationType.UNFRIEND,
				userDoug.getUsername() + " unfriended you.");
	}

	@AfterEach
	void tearDown() throws SQLException {
		cleanDB();
	}

	@Test
	void getLast50NotificationsF() throws Exception {
		MvcResult result = mockMvc.perform(get("/api/notification/friend/getLast50")
				.param("username", userAndrew.getUsername()))
				.andExpect(status().isOk())
				.andReturn();

		List<NotificationFPayload> notificationsF = getListOfNotificationFPayloadFromMvcResult(result);

		assertEquals(2, notificationsF.size());
		assertEquals(userDoug.getUsername(), notificationsF.get(0).getEmitterUsername());
		assertEquals(userBobby.getUsername(), notificationsF.get(1).getEmitterUsername());
	}

	@Test
	void removeNotificationF() throws Exception {
		Long notificationFId = insertNotificationFIntoDB(userBobby, userAndrew,
				NotificationType.FRIEND_REQUEST_DECLINED, "test description. not really important here tbh").getId();

		mockMvc.perform(delete("/api/notification/friend/remove")
				.param("id", notificationFId.toString()))
				.andExpect(status().isOk());

		Integer nbOfNotificationsF = countLast51NotificationsFOfAnUser(userAndrew);

		assertEquals(2, nbOfNotificationsF);
	}

	@Test
	void countLast51NotificationsF() throws Exception {
		MvcResult result = mockMvc.perform(get("/api/notification/friend/countLast51")
				.param("username", userAndrew.getUsername()))
				.andExpect(status().isOk())
				.andReturn();

		Integer nbOfNotificationsF = JsonPath.read(result.getResponse().getContentAsString(), "$");
		assertEquals(2, nbOfNotificationsF);
	}

	// ------------------------------------------------

	private void insertUserIntoDB(User user) {
		if (user != null) {
			userRepository.save(user);
		}
	}

	private Integer countLast51NotificationsFOfAnUser(User user) {
		return notificationRepository.countLast51NotificationsF(user,
				List.of(NotificationType.FRIEND_REQUEST, NotificationType.FRIEND_REQUEST_ACCEPTED,
						NotificationType.FRIEND_REQUEST_DECLINED, NotificationType.UNFRIEND));
	}

	private Notification insertNotificationFIntoDB(User receiver, User emitter, NotificationType type,
			String description) {
		return notificationRepository.save(
				Objects.requireNonNull(Notification.builder()
						.receiver(receiver)
						.emitter(emitter)
						.type(type)
						.description(description)
						.read(false)
						.build()));
	}

	private List<NotificationFPayload> getListOfNotificationFPayloadFromMvcResult(MvcResult result)
			throws JsonMappingException, JsonProcessingException, UnsupportedEncodingException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());

		var result_string = result.getResponse().getContentAsString();
		return mapper.readValue(result_string, new TypeReference<List<NotificationFPayload>>() {
		});
	}

	private void cleanDB() {
		notificationRepository.deleteAll();
		userRepository.deleteAll();
	}
}