package com.stevenst.app.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.sql.SQLException;

import org.h2.tools.Server;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.stevenst.app.model.Role;
import com.stevenst.app.repository.UserRepository;
import com.stevenst.app.service.JwtService;
import com.stevenst.app.util.TestUtil;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserControllerIntegrationTest {
	private static Server server;
	private static final String EMAIL = "testuser123";
	private static final String PASSWORD = "testpassword123";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private JwtService jwtService;

	@BeforeAll
	void init() throws Exception {
		server = Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092");
		server.start();

		TestUtil testUtil = new TestUtil(userRepository);
		testUtil.insertUserIntoDB(EMAIL, PASSWORD, "test", "user", Role.USER);
	}

	@AfterAll
	void tearDown() throws SQLException {
		userRepository.deleteAll();

		server.stop();
	}

	@Test
	@Transactional
	void getCurrentUserEndpoint() throws Exception {
		String token = jwtService.generateToken(EMAIL);

		mockMvc.perform(get("/api/user/currentUser")
				.header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.email").value(EMAIL));
	}

}