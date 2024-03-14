package com.stevenst.app.service.impl;

import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.stevenst.app.repository.CountryRepository;
import com.stevenst.app.repository.RegionRepository;
import com.stevenst.app.service.CountryAndRegionService;
import com.stevenst.app.util.CountryFromJson;
import com.stevenst.app.util.JsonUtil;
import com.stevenst.lib.exception.IgorIoException;
import com.stevenst.lib.model.Country;
import com.stevenst.lib.model.Region;
import com.stevenst.lib.payload.ResponsePayload;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CountryAndRegionServiceImpl implements CountryAndRegionService {
	@Value("${app.countries-and-regions-filename}")
	private String COUNTRIES_AND_REGIONS_FILENAME;
	private final RegionRepository regionRepository;
	private final CountryRepository countryRepository;

	@Override
	public List<Region> getAllRegionsByCountry(String countryName) {

		try {
			CountryFromJson country = JsonUtil.loadCountryAndRegionsFromJsonClasspath(countryName,
					COUNTRIES_AND_REGIONS_FILENAME);

			return country.getRegions();
		} catch (IOException e) {
			throw new IgorIoException(e.getMessage());
		}

	}

	@Override
	public ResponsePayload insertCountryAndRegionsIntoDb(String countryName) {
		boolean exists = countryRepository.existsByName(countryName);
		if (exists) {
			return ResponsePayload.builder().status(200).message("Country already exists.").build();	// TODO: status code for something that already exists (conflict??)
		}

		long count = regionRepository.countByCountryName(countryName);

		if (count < 1) {
			try {
				CountryFromJson countryFromJson = JsonUtil.loadCountryAndRegionsFromJsonClasspath(countryName,
						COUNTRIES_AND_REGIONS_FILENAME);

				Country country = countryFromJson.convertToCountry();
				List<Region> regions = countryFromJson.getRegions();

				countryRepository.save(country);
				regionRepository.saveAll(regions);

				// TODO: make the queries faster by introducing a new intermediary table with the country name and id (for quering for regions)

				return ResponsePayload.builder().status(200).message("Country and regions inserted successfully.").build();
			} catch (IOException e) {
				throw new IgorIoException(e.getMessage());
			}
		} else {
			return ResponsePayload.builder().status(200).message("Regions already exist.").build();
		}
	}

}
