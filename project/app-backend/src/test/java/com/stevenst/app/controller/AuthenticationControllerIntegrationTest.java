package com.stevenst.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stevenst.app.auth.AuthRequest;
import com.stevenst.app.auth.RegisterRequest;
import com.stevenst.app.exception.IgorAuthenticationException;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthenticationControllerIntegrationTest {
	private static Server server;
	private static final String EMAIL = "testuser123";
	private static final String PASSWORD = "testpassword123";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@BeforeAll
	void init() throws Exception {
		server = Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092");
		server.start();

		insertUserIntoDB();
	}

	@AfterAll
	void tearDown() throws SQLException {
		server.stop();
	}

	@Test
	@Transactional
	void registrationEndpoint() throws Exception {
		RegisterRequest registerRequest = new RegisterRequest("testuser123456", "testpassword123456", "test",
				"user");

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
		AuthRequest authenticationRequest = new AuthRequest("testuser123", "wrong_password");

		mockMvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(authenticationRequest)))
				.andExpect(status().isUnauthorized())
				.andExpect(result -> assertTrue(
						result.getResolvedException() instanceof IgorAuthenticationException))
				.andExpect(result -> assertEquals("Authentication failed",
						Objects.requireNonNull(result.getResolvedException()).getMessage()));
	}

	@Test
	@Transactional
	void registrationEndpointWithExistingEmail() throws Exception {
		RegisterRequest registerRequest = new RegisterRequest(EMAIL, PASSWORD, "test", "user");

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
	void registrationEndpointWithMissingCredentials() throws Exception {
		RegisterRequest registerRequest = new RegisterRequest("", "", "", "");

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
	void authenticationWithABadSignature() throws Exception {
		String badSignatureToken = generateTokenWithBadSignature();
		AuthRequest authenticationRequest = new AuthRequest(EMAIL, PASSWORD);

		mockMvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(authenticationRequest))
				.header("Authorization", "Bearer " + badSignatureToken))
				.andExpect(status().isUnauthorized())
				.andExpect(result -> assertEquals("Invalid Token",
						result.getResponse().getContentAsString()));
	}

	// ------------------------------------------------------------------------

	void insertUserIntoDB() throws Exception {
		RegisterRequest registerRequest = new RegisterRequest(EMAIL, PASSWORD, "test", "user");

		mockMvc.perform(post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(registerRequest)));
	}

	public String generateTokenWithBadSignature() {
		return Jwts
				.builder()
				.claims(new HashMap<>())
				.subject(EMAIL)
				.issuedAt(new Date(System.currentTimeMillis() - 1000))
				.expiration(new Date(System.currentTimeMillis() - 10))
				.signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(
						"1234abcd1234abcd1234abcd1234abcd1234abcd1234abcd1234abcd1234abcd")))
				.compact();
	}
}
