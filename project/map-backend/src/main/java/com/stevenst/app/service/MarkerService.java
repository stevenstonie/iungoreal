package com.stevenst.app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.stevenst.app.model.Marker;

@Service
public interface MarkerService {
	List<Marker> getAllMarkers();
	
	Marker addMarker(Marker marker);
}
