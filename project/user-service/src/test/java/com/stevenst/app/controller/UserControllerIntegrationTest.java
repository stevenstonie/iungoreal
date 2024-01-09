package com.stevenst.app.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.h2.tools.Server;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.jayway.jsonpath.JsonPath;
import com.stevenst.app.exception.IgorNotFoundException;
import com.stevenst.app.payload.UserPrivatePayload;
import com.stevenst.app.payload.UserPublicPayload;
import com.stevenst.app.repository.UserRepository;
import com.stevenst.lib.model.Role;
import com.stevenst.lib.model.User;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserControllerIntegrationTest {
	private static Server server;
	private static final User testUser = User.builder()
			.email("testemail123")
			.password("testpassword123")
			.username("testusername123").build();

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@BeforeAll
	void init() throws Exception {
		server = Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092");
		server.start();

		insertUserIntoDB(testUser);
	}

	@AfterAll
	void tearDown() throws SQLException {
		cleanDB();

		server.stop();
	}

	@Test
	void getUserPublicByUsername() throws Exception {
		var result = mockMvc.perform(
				get("/api/user/getPublicByUsername?username=" + testUser.getUsername()))
				.andExpect(status().isOk())
				.andReturn();

		UserPublicPayload user = getPublicUserFromMvcResult(result);

		assertNotNull(user.getUsername());
		assertNotNull(user.getCreatedAt());
		assertEquals(testUser.getUsername(), user.getUsername());
		assertTrue(Duration.between(user.getCreatedAt(), LocalDateTime.now()).toDays() < 1);
	}

	@Test
	void getUserPrivateByUsername() throws Exception {
		var result = mockMvc.perform(
				get("/api/user/getPrivateByUsername?username=" + testUser.getUsername()))
				.andExpect(status().isOk())
				.andReturn();

		UserPrivatePayload user = getPrivateUserFromMvcResult(result);

		assertNotNull(user.getUsername());
		assertNotNull(user.getEmail());
		assertNotNull(user.getRole());
		assertNotNull(user.getCreatedAt());
		assertEquals(testUser.getUsername(), user.getUsername());
		assertEquals(testUser.getEmail(), user.getEmail());
		assertEquals(Role.USER, user.getRole());
		assertTrue(Duration.between(user.getCreatedAt(), LocalDateTime.now()).toDays() < 1);
	}

	@Test
	void getUserByEmail() throws Exception {
		var result = mockMvc.perform(
				get("/api/user/getByEmail?email=" + testUser.getEmail()))
				.andExpect(status().isOk())
				.andReturn();

		UserPrivatePayload user = getPrivateUserFromMvcResult(result);

		assertNotNull(user.getUsername());
		assertNotNull(user.getEmail());
		assertNotNull(user.getRole());
		assertNotNull(user.getCreatedAt());
		assertEquals(testUser.getUsername(), user.getUsername());
		assertEquals(testUser.getEmail(), user.getEmail());
		assertEquals(Role.USER, user.getRole());
		assertTrue(Duration.between(user.getCreatedAt(), LocalDateTime.now()).toDays() < 1);
	}

	@ParameterizedTest
	@CsvSource({
			"/getPublicByUsername, username, nonexistenttestusername",
			"/getPrivateByUsername, username, nonexistenttestusername",
			"/getByEmail, email, nonexistenttestemail"
	})
	void allGetEndpoints_notFound(String endpoint, String paramType, String nameOrEmail) throws Exception {
		MvcResult result = mockMvc.perform(get("/api/user" + endpoint + "?" + paramType + "=" + nameOrEmail))
				.andExpect(status().isNotFound())
				.andReturn();
		Exception resolvedException = result.getResolvedException();

		assertNotNull(resolvedException);
		assertTrue(resolvedException instanceof IgorNotFoundException);
		assertEquals("User not found (with " + paramType + ": " + nameOrEmail + ")", resolvedException.getMessage());
	}

	// --------------------------------------------

	private void insertUserIntoDB(User user) {
		userRepository.save(user);
	}

	private void cleanDB() {
		userRepository.deleteAll();
	}

	private UserPrivatePayload getPrivateUserFromMvcResult(MvcResult result) throws UnsupportedEncodingException {
		var responseJson = JsonPath.parse(result.getResponse().getContentAsString());
		String username = responseJson.read("$.username");
		String email = responseJson.read("$.email");
		Role role = Role.valueOf(responseJson.read("$.role"));
		String createdAtString = responseJson.read("$.createdAt");
		LocalDateTime createdAt = LocalDateTime.parse(createdAtString, DateTimeFormatter.ISO_DATE_TIME);

		return UserPrivatePayload.builder()
				.username(username)
				.email(email)
				.role(role)
				.createdAt(createdAt)
				.build();
	}

	private UserPublicPayload getPublicUserFromMvcResult(MvcResult result) throws UnsupportedEncodingException {
		var responseJson = JsonPath.parse(result.getResponse().getContentAsString());
		String username = responseJson.read("$.username");
		String createdAtString = responseJson.read("$.createdAt");
		LocalDateTime createdAt = LocalDateTime.parse(createdAtString, DateTimeFormatter.ISO_DATE_TIME);

		return UserPublicPayload.builder()
				.username(username)
				.createdAt(createdAt)
				.build();
	}
}