package com.stevenst.app.service;

import java.util.List;

import com.stevenst.app.payload.RegionPayload;
import com.stevenst.lib.payload.ResponsePayload;

public interface CountryAndRegionService {
	public List<RegionPayload> getAllRegionsByCountry(Long countryId);

	public ResponsePayload insertCountryAndRegionsIntoDb(String country);
}
