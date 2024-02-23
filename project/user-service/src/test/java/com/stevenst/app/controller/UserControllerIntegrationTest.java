package com.stevenst.app.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
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
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.stevenst.lib.exception.IgorUserNotFoundException;
import com.stevenst.app.payload.UserPrivatePayload;
import com.stevenst.app.payload.UserPublicPayload;
import com.stevenst.app.repository.UserRepository;
import com.stevenst.app.service.UserService;
import com.stevenst.lib.model.Role;
import com.stevenst.lib.model.User;
import com.stevenst.lib.payload.ResponsePayload;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.http.AbortableInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

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
	void getUserPublicByUsername() throws Exception {
		MvcResult result = mockMvc.perform(
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
		MvcResult result = mockMvc.perform(
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
		MvcResult result = mockMvc.perform(
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

	@Test
	void saveProfilePicture() throws Exception {
		MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg",
				"file content".getBytes(StandardCharsets.UTF_8));

		when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
				.thenReturn(PutObjectResponse.builder().build());

		MvcResult result = mockMvc.perform(
				multipart("/api/user/saveProfilePicture")
						.file(file)
						.param("username", testUser.getUsername())
						.with(request -> {
							request.setMethod("PUT");
							return request;
						}))
				.andExpect(status().isOk()).andReturn();

		ResponsePayload response = getResponsePayloadFromMvcResult(result);
		assertEquals(200, response.getStatus());
		assertEquals("Profile picture uploaded successfully.", response.getMessage());
	}

	@Test
	void getProfilePictureLink() throws Exception {
		// ResponseInputStream<GetObjectResponse> mockResponseInputStream = new ResponseInputStream<>(
		// 		GetObjectResponse.builder().build(),
		// 		AbortableInputStream.create(new ByteArrayInputStream("mocked data".getBytes())));
		// when(s3Client.getObject(any(GetObjectRequest.class), any(ResponseTransformer.class)))
		// 		.thenReturn(mockResponseInputStream);

		// MvcResult result = mockMvc.perform(
		// 		multipart("/api/user/getProfilePictureLink")
		// 				.param("username", testUser.getUsername())
		// 				.with(request -> {
		// 					request.setMethod("GET");
		// 					return request;
		// 				}))
		// 		.andExpect(status().isOk()).andReturn();

		// String responseBody = result.getResponse().getContentAsString();
		// System.out.println(responseBody);

		// String response = getStringFromMvcResult(result);
		// assertEquals("mocked data", response);
		assertTrue(false);
	}

	@Test
	void removeProfilePicture() throws Exception {
		// Mockito.doNothing().when(s3Client).deleteObject(any(DeleteObjectRequest.class));

		// MvcResult result = mockMvc.perform(
		// 		multipart("/api/user/removeProfilePicture")
		// 				.param("username", testUser.getUsername())
		// 				.with(request -> {
		// 					request.setMethod("DELETE");
		// 					return request;
		// 				}))
		// 		.andExpect(status().isOk()).andReturn();

		// ResponsePayload response = getResponsePayloadFromMvcResult(result);
		// assertEquals(200, response.getStatus());
		// assertEquals("Removed profile picture for user: " + testUser.getUsername(), response.getMessage());
		assertTrue(false);
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
		assertTrue(resolvedException instanceof IgorUserNotFoundException);
		assertEquals("User not found (with " + paramType + ": " + nameOrEmail + ")", resolvedException.getMessage());
	}

	// --------------------------------------------

	private void insertUserIntoDB(User user) {
		userRepository.save(user);
	}

	private void cleanDB() {
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

	private String getStringFromMvcResult(MvcResult result) throws UnsupportedEncodingException {
		DocumentContext responseJson = JsonPath.parse(result.getResponse().getContentAsString());
		String string = responseJson.read("$.string");

		return string;
	}

	private UserPrivatePayload getPrivateUserFromMvcResult(MvcResult result) throws UnsupportedEncodingException {
		DocumentContext responseJson = JsonPath.parse(result.getResponse().getContentAsString());
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
		DocumentContext responseJson = JsonPath.parse(result.getResponse().getContentAsString());
		String username = responseJson.read("$.username");
		String createdAtString = responseJson.read("$.createdAt");
		LocalDateTime createdAt = LocalDateTime.parse(createdAtString, DateTimeFormatter.ISO_DATE_TIME);

		return UserPublicPayload.builder()
				.username(username)
				.createdAt(createdAt)
				.build();
	}
}