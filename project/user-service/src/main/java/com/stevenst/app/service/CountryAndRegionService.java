package com.stevenst.app.service;

import java.util.List;

import com.stevenst.app.payload.DetailedRegionPayload;
import com.stevenst.lib.payload.CountryOrRegionPayload;
import com.stevenst.lib.payload.ResponsePayload;

public interface CountryAndRegionService {
	List<CountryOrRegionPayload> getAvailableRegionsForUser(String username);

	CountryOrRegionPayload getCountryOfUser(String username);

	CountryOrRegionPayload getPrimaryRegionOfUser(String username);

	DetailedRegionPayload getPrimaryRegionDetailsOfUser(String username);

	List<CountryOrRegionPayload> getSecondaryRegionsOfUser(String username);

	ResponsePayload setCountryForUser(String username, Long countryId);

	ResponsePayload setPrimaryRegionOfUser(String username, Long regionId);

	ResponsePayload addSecondaryRegionForUser(String username, Long regionId);

	ResponsePayload removeCountryForUser(String username);

	ResponsePayload removePrimaryRegionForUser(String username);

	ResponsePayload removeSecondaryRegionForUser(String username, Long regionId);
}
