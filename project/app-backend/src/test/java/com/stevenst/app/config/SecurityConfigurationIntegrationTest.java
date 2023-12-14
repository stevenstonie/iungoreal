package com.stevenst.app.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityConfigurationIntegrationTest {
	/*
	* integration tests come after so finish this later.
	*/
	@Autowired
	private MockMvc mvc;

	@Test
	void whenGetNonWhitelistedEndpoint_then401() throws Exception {
		mvc.perform(get("/api/user/currentUser"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void whenOptionsCorsRequest_then200() throws Exception {
		mvc.perform(options("/api/user/currentUser")
				.header("Access-Control-Request-Headers", "Authorization")
				.header("Access-Control-Request-Method", "GET")
				.header("Origin", "http://localhost:4200"))
				.andExpect(status().isOk());
	}

	// @Test
	// void whenPostLogin_then200() throws Exception {
	// 	String jsonPayload = "{\"username\":\"user1\",\"password\":\"password\"}";

	// 	mvc.perform(post("/api/auth/login")
	// 			.contentType(MediaType.APPLICATION_JSON)
	// 			.content(jsonPayload))
	// 			.andExpect(status().isOk());
	// }
}