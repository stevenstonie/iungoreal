package com.stevenst.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stevenst.app.exception.IgorAuthenticationException;
import com.stevenst.app.payload.AuthRequest;
import com.stevenst.app.payload.RegisterRequest;
import com.stevenst.lib.model.Role;
import com.stevenst.app.repository.AuthRepository;
import com.stevenst.app.util.TestUtil;

import jakarta.transaction.Transactional;
import org.h2.tools.Server;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.SQLException;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthenticationControllerIntegrationTest {
	private static Server server;
	private static final String EMAIL = "testemail123";
	private static final String PASSWORD = "testpassword123";
	private static final String USERNAME = "testusername123";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private AuthRepository authRepository;

	@BeforeAll
	void init() throws Exception {
		server = Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092");
		server.start();

		TestUtil testUtil = new TestUtil(authRepository);
		testUtil.insertUserIntoDB(EMAIL, PASSWORD, USERNAME, Role.USER);
	}

	@AfterAll
	void tearDown() throws SQLException {
		authRepository.deleteAll();

		server.stop();
	}

	@Test
	@Transactional
	void registrationEndpoint() throws Exception {
		RegisterRequest registerRequest = new RegisterRequest("testemail123456", "testpassword123456",
				"testusername123456");

		mockMvc.perform(post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(registerRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token").isNotEmpty());
	}

	@Test
	@Transactional
	void authenticationEndpoint() throws Exception {
		AuthRequest authenticationRequest = new AuthRequest(EMAIL, PASSWORD);

		mockMvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(authenticationRequest)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token").isNotEmpty());
	}

	@Test
	@Transactional
	void authenticationEndpointWithInvalidCredentials() throws Exception {
		AuthRequest authenticationRequest = new AuthRequest(EMAIL, "wrong_password");

		mockMvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(authenticationRequest)))
				.andExpect(status().isUnauthorized())
				.andExpect(result -> assertTrue(
						result.getResolvedException() instanceof IgorAuthenticationException))
				.andExpect(result -> assertEquals("Invalid credentials",
						Objects.requireNonNull(result.getResolvedException()).getMessage()));
	}

	@Test
	@Transactional
	void registrationEndpointWithExistingEmail() throws Exception {
		RegisterRequest registerRequest = new RegisterRequest(EMAIL, PASSWORD, "new_username");

		mockMvc.perform(post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(registerRequest)))
				.andExpect(status().isUnauthorized())
				.andExpect(result -> assertTrue(
						result.getResolvedException() instanceof IgorAuthenticationException))
				.andExpect(result -> assertEquals("Email already taken",
						Objects.requireNonNull(result.getResolvedException()).getMessage()));
	}

	@Test
	@Transactional
	void registrationEndpointWithExistingUsername() throws Exception {
		RegisterRequest registerRequest = new RegisterRequest("new_email", PASSWORD, USERNAME);

		mockMvc.perform(post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(registerRequest)))
				.andExpect(status().isUnauthorized())
				.andExpect(result -> assertTrue(
						result.getResolvedException() instanceof IgorAuthenticationException))
				.andExpect(result -> assertEquals("Username already taken",
						Objects.requireNonNull(result.getResolvedException()).getMessage()));
	}

	@Test
	@Transactional
	void registrationEndpointWithMissingCredentials() throws Exception {
		RegisterRequest registerRequest = new RegisterRequest("", "", "");

		mockMvc.perform(post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(registerRequest)))
				.andExpect(status().isUnauthorized())
				.andExpect(result -> assertTrue(
						result.getResolvedException() instanceof IgorAuthenticationException))
				.andExpect(result -> assertEquals("Credentials cannot be empty",
						Objects.requireNonNull(result.getResolvedException()).getMessage()));
	}

	@Test
	@Transactional
	void registrationEndpointWithNullCredentials() throws Exception {
		RegisterRequest registerRequest = new RegisterRequest(null, null, null);

		mockMvc.perform(post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(registerRequest)))
				.andExpect(status().isUnauthorized())
				.andExpect(result -> assertTrue(
						result.getResolvedException() instanceof IgorAuthenticationException))
				.andExpect(result -> assertEquals("Credentials cannot be empty",
						Objects.requireNonNull(result.getResolvedException()).getMessage()));
	}

	@Test
	@Transactional
	void authenticationEndpointWithMissingCredentials() throws Exception {
		AuthRequest authenticationRequest = new AuthRequest("", "");

		mockMvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(authenticationRequest)))
				.andExpect(status().isUnauthorized())
				.andExpect(result -> assertTrue(
						result.getResolvedException() instanceof IgorAuthenticationException))
				.andExpect(result -> assertEquals("Credentials cannot be empty",
						Objects.requireNonNull(result.getResolvedException()).getMessage()));
	}

	@Test
	@Transactional
	void authenticationEndpointWithNullCredentials() throws Exception {
		AuthRequest authenticationRequest = new AuthRequest(null, null);

		mockMvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(authenticationRequest)))
				.andExpect(status().isUnauthorized())
				.andExpect(result -> assertTrue(
						result.getResolvedException() instanceof IgorAuthenticationException))
				.andExpect(result -> assertEquals("Credentials cannot be empty",
						Objects.requireNonNull(result.getResolvedException()).getMessage()));
	}

	@Test
	@Transactional
	void authenticationWithABadSignature() throws Exception {
		AuthRequest authenticationRequest = new AuthRequest(EMAIL, PASSWORD);
		TestUtil testUtil = new TestUtil(authRepository);

		mockMvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(authenticationRequest))
				.header("Authorization", "Bearer " + testUtil.generateTokenWithBadSignature(EMAIL)))
				.andExpect(status().isUnauthorized())
				.andExpect(result -> assertEquals("Invalid token signature",
						result.getResponse().getContentAsString()));
	}

	// TODO: test an expired token
}
// TODO: add a unit test class for this class as well
