package com.stevenst.app.service;

import java.util.List;

import com.stevenst.lib.model.Region;
import com.stevenst.lib.payload.ResponsePayload;

public interface CountryAndRegionService {
	public List<Region> getAllRegionsByCountry(String country);

	public ResponsePayload insertCountryAndRegionsIntoDb(String country);
}
