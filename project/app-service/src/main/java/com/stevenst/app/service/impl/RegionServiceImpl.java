package com.stevenst.app.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.stevenst.app.service.RegionService;
import com.stevenst.app.util.JsonUtil;
import com.stevenst.lib.exception.IgorIoException;
import com.stevenst.lib.model.Region;

@Service
public class RegionServiceImpl implements RegionService {
	private static final String REGIONS_PATH = "regions.json";

	@Override
	public List<Region> getAllRegionsByCountry(String country) {
		List<Region> allEntries;

		try {
			allEntries = JsonUtil.loadEntriesFromClasspath(REGIONS_PATH);
		} catch (IOException e) {
			throw new IgorIoException(e.getMessage());
		}

		return allEntries.stream()
				.filter(region -> country.equals(region.getCountry_name()))
				.collect(Collectors.toList());
	}

}
