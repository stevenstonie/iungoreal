package com.stevenst.app.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

import org.h2.tools.Server;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.stevenst.app.repository.UserRepository;
import com.stevenst.app.repository.post.PostMediaRepository;
import com.stevenst.app.repository.post.PostRepository;
import com.stevenst.lib.model.User;
import com.stevenst.lib.payload.ResponsePayload;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PostControllerIntegrationTest {
	private static Server server;
	private static final User testUser = User.builder()
			.email("testemail123")
			.password("testpassword123")
			.username("testusername123").build();

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PostRepository postRepository;
	@Autowired
	private PostMediaRepository postMediaRepository;

	@MockBean
	private S3Client s3Client;

	@BeforeAll
	void init() throws Exception {
		MockitoAnnotations.openMocks(this);
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
	void createPost() throws Exception {
		MockMultipartFile file1 = new MockMultipartFile("file", "test.jpg", "image/jpeg",
				"file content".getBytes(StandardCharsets.UTF_8));
		MockMultipartFile file2 = new MockMultipartFile("file2", "test2.jpg", "image/jpeg",
				"file content 2".getBytes(StandardCharsets.UTF_8));

		when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
				.thenReturn(PutObjectResponse.builder().build());

		MvcResult result = mockMvc.perform(
				multipart("/api/post/create")
						.file("files", file1.getBytes())
						.file("files", file2.getBytes())
						.param("authorUsername", testUser.getUsername())
						.param("title", "test title 123")
						.param("description", "test description 123")
						.with(request -> {
							request.setMethod("POST");
							return request;
						}))
				.andExpect(status().isOk()).andReturn();

		ResponsePayload response = getResponsePayloadFromMvcResult(result);
		assertEquals(201, response.getStatus());
		assertEquals("Post created successfully for " + testUser.getUsername() + ".", response.getMessage());
	}

	// ----------------------------------------

	private void insertUserIntoDB(User user) {
		userRepository.save(user);
	}

	private void cleanDB() {
		postMediaRepository.deleteAll();
		postRepository.deleteAll();
		userRepository.deleteAll();
	}

	private ResponsePayload getResponsePayloadFromMvcResult(MvcResult result) throws UnsupportedEncodingException {
		DocumentContext responseJson = JsonPath.parse(result.getResponse().getContentAsString());
		String message = responseJson.read("$.message");
		int status = responseJson.read("$.status");

		return ResponsePayload.builder()
				.message(message)
				.status(status)
				.build();
	}
}
