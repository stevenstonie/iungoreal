package com.stevenst.app.util;

import com.stevenst.app.model.Marker;
import com.stevenst.app.repository.MarkerRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TestUtil {
	private final MarkerRepository markerRepository;
	
	public void insertMarkerIntoDB(Marker marker) {
		markerRepository.save(marker);
	}
}
