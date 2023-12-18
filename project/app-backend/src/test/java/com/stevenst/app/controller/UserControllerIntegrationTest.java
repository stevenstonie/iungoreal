package com.stevenst.app.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.SQLException;

import org.h2.tools.Server;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import com.stevenst.app.model.Role;
import com.stevenst.app.repository.UserRepository;
import com.stevenst.app.service.UserService;
import com.stevenst.app.service.impl.JwtServiceImpl;
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
	private UserRepository userRepository;

	@MockBean
	private JwtServiceImpl jwtService;

	@MockBean
	private UserService userService;

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
		assertEquals(3, 1 + 2);
	}

}