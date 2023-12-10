package com.stevenst.app.service;

import org.springframework.stereotype.Service;

import com.stevenst.app.model.Marker;

@Service
public interface MarkerService {
	Marker addMarker(Marker marker);
}
