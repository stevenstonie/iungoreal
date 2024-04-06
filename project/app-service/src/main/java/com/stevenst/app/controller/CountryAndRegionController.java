package com.stevenst.app.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stevenst.app.service.CountryAndRegionService;
import com.stevenst.lib.model.Country;
import com.stevenst.lib.payload.CountryOrRegionPayload;
import com.stevenst.lib.payload.ResponsePayload;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/country")
@RequiredArgsConstructor
public class CountryAndRegionController {
	private final CountryAndRegionService countryAndRegionService;

	@GetMapping("/getAll")
	public ResponseEntity<List<Country>> getAllCountries() {
		return ResponseEntity.ok(countryAndRegionService.getAllCountries());
	}

	@GetMapping("/getAllRegions/byCountryName")
	public ResponseEntity<List<CountryOrRegionPayload>> getAllRegionPayloadsByCountryName(
			@RequestParam String countryName) {
		return ResponseEntity.ok(countryAndRegionService.getAllRegionPayloadsByCountryName(countryName));
	}

	// only used by ADMIN
	@PostMapping("/insertCountryAndRegions/byCountryName")
	public ResponseEntity<ResponsePayload> insertAllRegionsOfACountryIntoDb(@RequestParam String countryName) {
		return ResponseEntity.ok(countryAndRegionService.insertCountryAndRegionsIntoDb(countryName));
	}

	// only used by ADMIN
	@DeleteMapping("/removeCountryAndItsRegions")
	public ResponseEntity<ResponsePayload> removeCountryAndItsRegions(@RequestParam String countryName) {
		return ResponseEntity.ok(countryAndRegionService.removeCountryAndItsRegions(countryName));
	}
}
