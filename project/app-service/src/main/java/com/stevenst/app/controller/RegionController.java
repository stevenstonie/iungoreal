package com.stevenst.app.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stevenst.app.payload.RegionPayload;
import com.stevenst.app.service.CountryAndRegionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/region")
@RequiredArgsConstructor
public class RegionController {
	private final CountryAndRegionService countryAndRegionService;
	
	@GetMapping("/getAll/byCountryId")
	public ResponseEntity<List<RegionPayload>> getAllRegionsByCountryId(@RequestParam Long countryId) {
		return ResponseEntity.ok(countryAndRegionService.getAllRegionsByCountryId(countryId));
	}
	
}
