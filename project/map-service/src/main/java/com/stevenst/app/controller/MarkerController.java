package com.stevenst.app.controller;

import org.springframework.web.bind.annotation.RestController;

import com.stevenst.app.exception.IgorMarkerException;
import com.stevenst.app.model.Marker;
import com.stevenst.app.service.MarkerService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
		try {
			return markerService.addMarker(marker);
		} catch (DataIntegrityViolationException ex) {
			throw new IgorMarkerException(ex.getMessage());
		}
	}

	@GetMapping("/getMarker")
	public Marker getMarker(@RequestParam Long id) {
		return markerService.getMarker(id);
	}
}
