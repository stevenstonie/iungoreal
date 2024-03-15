package com.stevenst.app.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stevenst.app.payload.RegionPayload;
import com.stevenst.app.service.CountryAndRegionService;
import com.stevenst.lib.model.Region;
import com.stevenst.lib.payload.ResponsePayload;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/country")
@RequiredArgsConstructor
public class CountryAndRegionController {
	private final CountryAndRegionService countryAndRegionService;

	@GetMapping("/getAllRegions/byCountryId")
	public ResponseEntity<List<RegionPayload>> getAllRegionsByCountry(@RequestParam Long countryId) {
		return ResponseEntity.ok(countryAndRegionService.getAllRegionsByCountry(countryId));
	}

	// get all countries

	// get secondary regions of user

	// set primary region for user
	
	// set secondary region for user
	
	// remove secondary region of user

	@PostMapping("/insertAllRegions/byCountryName")
	public ResponseEntity<ResponsePayload> insertAllRegionsOfACountryIntoDb(@RequestParam String countryName) {
		return ResponseEntity.ok(countryAndRegionService.insertCountryAndRegionsIntoDb(countryName));
	}
}
