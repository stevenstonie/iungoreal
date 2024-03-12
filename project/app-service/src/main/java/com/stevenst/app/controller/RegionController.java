package com.stevenst.app.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stevenst.app.service.RegionService;
import com.stevenst.lib.model.Region;
import com.stevenst.lib.payload.ResponsePayload;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/region")
@RequiredArgsConstructor
public class RegionController {
	private final RegionService regionService;

	@GetMapping("/getAll/byCountry")
	public ResponseEntity<List<Region>> getAllRegionsByCountry(@RequestParam String country) {
		return ResponseEntity.ok(regionService.getAllRegionsByCountry(country));
	}

	@PostMapping("/insertAll/byCountry")
	public ResponseEntity<ResponsePayload> insertAllRegionsOfACountryIntoDb(@RequestParam String country) {
		return ResponseEntity.ok(regionService.insertAllRegionsOfACountryIntoDb(country));
	}
}
