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
@RequestMapping("/api/marker")
@RequiredArgsConstructor
public class MarkerController {
	private final MarkerService markerService;

	@GetMapping("/getAll")
	public List<Marker> getAll() {
		return markerService.getAllMarkers();
	}

	@PostMapping("/add")
	public Marker addMarker(@RequestBody Marker marker) {
		try {
			return markerService.addMarker(marker);
		} catch (DataIntegrityViolationException ex) {
			throw new IgorMarkerException(ex.getMessage());
		}
	}

	@GetMapping("/get")
	public Marker getMarker(@RequestParam Long id) {
		return markerService.getMarker(id);
	}
}
