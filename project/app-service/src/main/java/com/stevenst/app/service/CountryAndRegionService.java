package com.stevenst.app.service;

import java.util.List;

import com.stevenst.lib.model.Country;
import com.stevenst.lib.payload.CountryOrRegionPayload;
import com.stevenst.lib.payload.ResponsePayload;

public interface CountryAndRegionService {
	public List<Country> getAllCountries();

	public List<CountryOrRegionPayload> getAllRegionPayloadsByCountryName(String countryName);

	public ResponsePayload insertCountryAndRegionsIntoDb(String countryName);
}
