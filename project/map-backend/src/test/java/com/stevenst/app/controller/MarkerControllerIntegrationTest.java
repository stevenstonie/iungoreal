package com.stevenst.app.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.LocalDateTime;

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
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stevenst.app.model.Marker;
import com.stevenst.app.repository.MarkerRepository;
import com.stevenst.app.util.TestUtil;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MarkerControllerIntegrationTest {
	private static Server server;
	private static Marker marker = new Marker(0L, "title", "description", 12.345, 67.890,
			LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(3));
	private TestUtil testUtil;

	@Autowired
	private MarkerRepository markerRepository;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private MockMvc mockMvc;

	@BeforeAll
	void init() throws Exception {
		server = Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092");
		server.start();

		testUtil = new TestUtil(markerRepository);
		Long idOfMarkerFromDb = testUtil.insertMarkerIntoDB(marker).getId();
		marker.setId(idOfMarkerFromDb);
	}

	@AfterAll
	void tearDown() throws SQLException {
		markerRepository.deleteAll();

		server.stop();
	}

	@Test
	void testGetAllMarkers() throws Exception {
		testUtil.insertMarkerIntoDB(marker);

		MvcResult result = mockMvc.perform(get("/api/markers/"))
				.andExpect(status().isOk())
				.andReturn();

		String responseContent = result.getResponse().getContentAsString();
		JsonNode jsonNode = objectMapper.readValue(responseContent, JsonNode.class);
		int arraySize = jsonNode.size();

		assertTrue(arraySize > 0);
	}

	@Test
	void testAddMarker() throws Exception {
		Marker marker = new Marker(0L, "test_title", "test_description", 11.111, 22.222,
				LocalDateTime.now(), LocalDateTime.now().plusDays(1));

		var response = mockMvc.perform(post("/api/markers/addMarker")
				.contentType("application/json")
				.content(testUtil.convertObjectToJsonBytes(marker)))
				.andExpect(status().isOk());

		int idOfMarker = objectMapper.readTree(response.andReturn().getResponse().getContentAsString())
				.get("id").asInt();

		mockMvc.perform(get("/api/markers/getMarker")
				.param("id", String.valueOf(idOfMarker)))
				.andExpect(status().isOk());
	}

	@Test
	void testGetMarker() throws Exception {
		mockMvc.perform(get("/api/markers/getMarker")
				.param("id", String.valueOf(marker.getId())))
				.andExpect(status().isOk());
	}

	@Test
	void testGetMarker_latAndLngValuesAsExpected() throws Exception {
		var response = mockMvc.perform(get("/api/markers/getMarker")
				.param("id", String.valueOf(marker.getId())))
				.andExpect(status().isOk());

		Marker markerFromDb = objectMapper
				.readValue(response.andReturn().getResponse().getContentAsString(), Marker.class);

		BigDecimal expectedLatitude = BigDecimal.valueOf(marker.getLatitude()).setScale(3, RoundingMode.HALF_UP);
		BigDecimal actualLatitude = BigDecimal.valueOf(markerFromDb.getLatitude()).setScale(3, RoundingMode.HALF_UP);
		BigDecimal expectedLongitude = BigDecimal.valueOf(marker.getLongitude()).setScale(3, RoundingMode.HALF_UP);
		BigDecimal actualLongitude = BigDecimal.valueOf(markerFromDb.getLongitude()).setScale(3, RoundingMode.HALF_UP);

		assertEquals(expectedLatitude, actualLatitude);
		assertEquals(expectedLongitude, actualLongitude);
	}
}
