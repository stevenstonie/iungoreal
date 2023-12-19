package com.stevenst.app.controller;

import java.sql.SQLException;
import java.time.LocalDate;

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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.stevenst.app.model.Marker;
import com.stevenst.app.repository.MarkerRepository;
import com.stevenst.app.util.TestUtil;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MarkerControllerIntegrationTest {
	private static Server server;
	private static final Marker marker = new Marker(0, "title", "description", 12.345, 67.890,
			LocalDate.now().plusDays(1), LocalDate.now().plusDays(3));

	@Autowired
	private MarkerRepository markerRepository;

	@Autowired
	private MockMvc mockMvc;

	@BeforeAll
	void init() throws Exception {
		server = Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092");
		server.start();

		TestUtil testUtil = new TestUtil(markerRepository);
		testUtil.insertMarkerIntoDB(marker);
	}

	@AfterAll
	void tearDown() throws SQLException {
		markerRepository.deleteAll();

		server.stop();
	}

	@Test
	void testGetAllMarkers() throws Exception {
		mockMvc.perform(get("/api/markers/"))
				.andExpect(status().isOk());
	}

	@Test
	void testAddMarker() throws Exception {
		
	}
}
