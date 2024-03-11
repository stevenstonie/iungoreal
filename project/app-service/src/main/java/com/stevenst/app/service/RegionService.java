package com.stevenst.app.service;

import java.util.List;

import com.stevenst.lib.model.Region;

public interface RegionService {
	public List<Region> getAllRegionsByCountry(String country);
}
