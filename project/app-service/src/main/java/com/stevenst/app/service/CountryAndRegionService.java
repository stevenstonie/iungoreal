package com.stevenst.app.service;

import java.util.List;

import com.stevenst.app.payload.RegionPayload;
import com.stevenst.lib.payload.ResponsePayload;

public interface CountryAndRegionService {
	public List<RegionPayload> getAllRegionsByCountry(String country);

	public ResponsePayload insertCountryAndRegionsIntoDb(String country);
}
