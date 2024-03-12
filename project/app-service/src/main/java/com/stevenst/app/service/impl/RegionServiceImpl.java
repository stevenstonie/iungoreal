package com.stevenst.app.service.impl;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.stevenst.app.repository.RegionRepository;
import com.stevenst.app.service.RegionService;
import com.stevenst.app.util.JsonUtil;
import com.stevenst.lib.exception.IgorIoException;
import com.stevenst.lib.model.Region;
import com.stevenst.lib.payload.ResponsePayload;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegionServiceImpl implements RegionService {
	private static final String REGIONS_PATH = "regions.json";
	private final RegionRepository regionRepository;

	@Override
	public List<Region> getAllRegionsByCountry(String country) {
		List<Region> allEntries;

		try {
			allEntries = JsonUtil.loadRegionsFromJsonClasspath(REGIONS_PATH);
		} catch (IOException e) {
			throw new IgorIoException(e.getMessage());
		}

		return allEntries.stream()
				.filter(region -> country.equals(region.getCountryName()))
				.collect(Collectors.toList());
	}

	@Override
	public ResponsePayload insertAllRegionsOfACountryIntoDb(String country) {
		List<Region> allEntries;

		long count = regionRepository.countByCountryName(country);

		if (count < 1) {
			try {
				allEntries = JsonUtil.loadRegionsFromJsonClasspath(REGIONS_PATH);
				allEntries = allEntries.stream()
						.filter(region -> country.equals(region.getCountryName()))
						.collect(Collectors.toList());
				regionRepository.saveAll(allEntries);

				return ResponsePayload.builder().status(200).message("Regions inserted successfully.").build();
			} catch (IOException e) {
				throw new IgorIoException(e.getMessage());
			}
		} else {
			return ResponsePayload.builder().status(200).message("Regions already exist.").build();
		}
	}

}
