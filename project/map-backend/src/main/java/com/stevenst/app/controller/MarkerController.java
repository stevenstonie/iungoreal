package com.stevenst.app.controller;

import org.springframework.web.bind.annotation.RestController;

import com.stevenst.app.model.Marker;
import com.stevenst.app.service.MarkerService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/markers")
@RequiredArgsConstructor
public class MarkerController {
	private final MarkerService markerService;

	@GetMapping("/")
	public List<Marker> getAllMarkers() {
		return markerService.getAllMarkers();
	}

	@PostMapping("/addMarker")
	public Marker addMarker(@RequestBody Marker marker) {
		return markerService.addMarker(marker);
	}
}
