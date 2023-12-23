package com.stevenst.app.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigurationIntegrationTest {
	@Autowired
	private MockMvc mockMvc;

	@Test
	void whenGetNonWhitelistedEndpoint_then401() throws Exception {
		mockMvc.perform(get("/api/user/currentUser"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void whenOptionsCorsRequest_then200() throws Exception {
		mockMvc.perform(options("/api/user/currentUser")
				.header("Access-Control-Request-Headers", "Authorization")
				.header("Access-Control-Request-Method", "GET")
				.header("Origin", "http://localhost:4200"))
				.andExpect(status().isOk());
	}
}