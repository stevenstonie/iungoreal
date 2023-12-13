package com.stevenst.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stevenst.app.auth.AuthenticationRequest;
import com.stevenst.app.auth.RegisterRequest;
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
    void testRegistrationEndpoint() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("testuser123456", "testpassword123456", "test", "user");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    @Transactional
    void testAuthenticationEndpoint() throws Exception {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(EMAIL, PASSWORD);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authenticationRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    @Transactional
    void testAuthenticationEndpointWithInvalidCredentials() throws Exception {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest("testuser123",
                "wrongPassword");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authenticationRequest)))
                .andExpect(status().isUnauthorized());
                // TODO: add another assertion for a thrown exception
    }

    // ------------------------------------------------------------------------

    void insertUserIntoDB() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest(EMAIL, PASSWORD, "test", "user");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }
}
