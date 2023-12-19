package com.stevenst.app.util;

import java.util.Date;

import com.stevenst.app.model.Marker;
import com.stevenst.app.repository.MarkerRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TestUtil {
	private final MarkerRepository markerRepository;

	public void insertMarkerIntoDB(String title, String description, double latitude, double longitude, Date startDate, Date endDate) {
		Marker marker = new Marker(0, title, description, latitude, longitude, startDate, endDate);
		
		markerRepository.save(marker);
	}
}
