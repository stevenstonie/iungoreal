package com.stevenst.app.service;

import java.util.List;

import com.stevenst.app.payload.RegionPayload;
import com.stevenst.lib.model.Country;
import com.stevenst.lib.payload.ResponsePayload;

public interface CountryAndRegionService {
	public List<Country> getAllCountries();

	public List<RegionPayload> getAllRegionsByCountryId(Long countryId);

	public ResponsePayload insertCountryAndRegionsIntoDb(String countryName);
}
