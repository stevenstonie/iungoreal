package com.stevenst.app.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.stevenst.app.model.Marker;
import com.stevenst.app.repository.MarkerRepository;

public class MarkerServiceImplTest {
	@Mock
	private MarkerRepository markerRepository;

	@InjectMocks
	private MarkerServiceImpl markerService;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void getAllMarkers() {
		List<Marker> markers = Arrays.asList(
				Marker.builder().id(-1L).title("Marker One").description("Description One").latitude(34.0522)
						.longitude(-118.2437).startDate(LocalDateTime.now().plusDays(1))
						.endDate(LocalDateTime.now().plusDays(2)).build(),
				Marker.builder().id(-2L).title("Marker Two").description("Description Two").latitude(40.7128)
						.longitude(-74.0060).startDate(LocalDateTime.now().plusDays(1))
						.endDate(LocalDateTime.now().plusDays(2)).build(),
				Marker.builder().id(-3L).title("Marker Three").description("Description Three").latitude(37.7749)
						.longitude(-122.4194).startDate(LocalDateTime.now().plusDays(1))
						.endDate(LocalDateTime.now().plusDays(2)).build());

		when(markerRepository.findAll()).thenReturn(markers);

		List<Marker> result = markerService.getAllMarkers();

		assertEquals(markers, result);
	}

	@Test
	void addMarker() {
		Marker marker = Marker.builder().id(-1L).title("Marker One").description("Description One").latitude(34.0522)
				.longitude(-118.2437).startDate(LocalDateTime.now().plusDays(1))
				.endDate(LocalDateTime.now().plusDays(2))
				.build();

		when(markerRepository.save(marker)).thenReturn(marker);

		Marker result = markerService.addMarker(marker);

		assertEquals(marker, result);
	}
	
	@Test
	void getMarker() {
		Marker marker = Marker.builder().id(-1L).title("Marker One").description("Description One").latitude(34.0522)
				.longitude(-118.2437).startDate(LocalDateTime.now().plusDays(1))
				.endDate(LocalDateTime.now().plusDays(2))
				.build();

		when(markerRepository.findById(marker.getId())).thenReturn(Optional.of(marker));

		Marker result = markerService.getMarker(marker.getId());

		assertEquals(marker, result);
	}
	
	@Test
	void getAllMarkers_emptyList() {
		List<Marker> markers = Arrays.asList();

		when(markerRepository.findAll()).thenReturn(markers);

		List<Marker> result = markerService.getAllMarkers();

		assertEquals(markers, result);
	}

	@Test
	void addMarker_null() {
		Marker marker = null;

		Marker result = markerService.addMarker(marker);

		assertEquals(null, result);
	}
	
	@Test
	void getMarker_notFound() {
		Marker marker = Marker.builder().id(-1L).title("Marker One").description("Description One").latitude(34.0522)
				.longitude(-118.2437).startDate(LocalDateTime.now().plusDays(1))
				.endDate(LocalDateTime.now().plusDays(2))
				.build();

		when(markerRepository.findById(marker.getId())).thenReturn(Optional.empty());

		Marker result = markerService.getMarker(marker.getId());

		assertEquals(null, result);
	}
}
