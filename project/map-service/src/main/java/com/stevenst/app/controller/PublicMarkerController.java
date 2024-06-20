package com.stevenst.app.controller;

import org.springframework.web.bind.annotation.RestController;

import com.stevenst.app.model.Marker;
import com.stevenst.app.payload.ResponsePayload;
import com.stevenst.app.service.MarkerService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/marker")
@RequiredArgsConstructor
public class PublicMarkerController {
	private final MarkerService markerService;

	@GetMapping("/getAll")
	public ResponseEntity<List<Marker>> getAll() {
		return ResponseEntity.ok(markerService.getAllMarkers());
	}

	@PostMapping("/add")
	public ResponseEntity<Marker> addMarker(@RequestBody Marker marker) {
		return ResponseEntity.ok(markerService.addMarker(marker));
	}

	@GetMapping("/get")
	public ResponseEntity<Marker> getMarker(@RequestParam Long id) {
		return ResponseEntity.ok(markerService.getMarker(id));
	}

	@DeleteMapping("/remove")
	public ResponseEntity<ResponsePayload> removeMarker(@RequestParam Long id) {
		return ResponseEntity.ok(markerService.removeMarker(id));
	}
}
