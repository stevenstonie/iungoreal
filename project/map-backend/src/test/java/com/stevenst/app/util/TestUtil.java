package com.stevenst.app.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.stevenst.app.model.Marker;
import com.stevenst.app.repository.MarkerRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TestUtil {
	private final MarkerRepository markerRepository;
	
	public Marker insertMarkerIntoDB(Marker marker) {
		return markerRepository.save(marker);
	}
	
	public byte[] convertObjectToJsonBytes(Object object) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());

		return mapper.writeValueAsBytes(object);
	}
}
