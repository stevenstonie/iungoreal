package com.stevenst.app.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.stevenst.app.repository.CountryRepository;
import com.stevenst.app.repository.RegionRepository;
import com.stevenst.app.repository.SecondaryRegionsUsersRepository;
import com.stevenst.app.repository.UserRepository;
import com.stevenst.app.service.CountryAndRegionService;
import com.stevenst.app.util.CountryFromJson;
import com.stevenst.app.util.JsonUtil;
import com.stevenst.lib.exception.IgorEntityAlreadyExistsException;
import com.stevenst.lib.exception.IgorEntityNotFoundException;
import com.stevenst.lib.exception.IgorIoException;
import com.stevenst.lib.exception.IgorNoContentToRemoveException;
import com.stevenst.lib.model.Country;
import com.stevenst.lib.model.Region;
import com.stevenst.lib.payload.CountryOrRegionPayload;
import com.stevenst.lib.payload.ResponsePayload;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CountryAndRegionServiceImpl implements CountryAndRegionService {
	@Value("${app.countries-and-regions-filename}")
	private String COUNTRIES_AND_REGIONS_FILENAME;
	private final RegionRepository regionRepository;
	private final CountryRepository countryRepository;
	private final UserRepository userRepository;
	private final SecondaryRegionsUsersRepository secondaryRegionsUsersRepository;

	@Override
	public List<Country> getAllCountries() {
		return countryRepository.findAll();
	}

	@Override
	public List<CountryOrRegionPayload> getAllRegionPayloadsByCountryName(String countryName) {
		Country country = countryRepository.findByName(countryName);
		if (country == null) {
			throw new IgorEntityNotFoundException("Country not found.");
		}

		List<Region> regions = regionRepository.findAllByCountry(country);

		List<CountryOrRegionPayload> regionPayloads = new ArrayList<>();
		for (Region region : regions) {
			regionPayloads.add(CountryOrRegionPayload.builder()
					.id(region.getId())
					.name(region.getName())
					.build());
		}

		return regionPayloads;
	}

	@Override
	public ResponsePayload insertCountryAndRegionsIntoDb(String countryName) {
		Country country = countryRepository.findByName(countryName);
		if (country != null) {
			throw new IgorEntityAlreadyExistsException("Country with name: " + countryName + " already exists.");
		}

		long count = regionRepository.countByCountry(country);

		if (count < 1) {
			try {
				CountryFromJson countryFromJson = JsonUtil.loadCountryAndRegionsFromJsonClasspath(countryName,
						COUNTRIES_AND_REGIONS_FILENAME);
				if (countryFromJson == null) {
					throw new IgorEntityNotFoundException("Country with name: " + countryName + " has not been found.");
				}

				Country countryToInsertIntoDb = countryFromJson.convertToCountry();
				List<Region> regionsToInsertIntoDb = countryFromJson.getRegions();
				for (Region region : regionsToInsertIntoDb) {
					region.setCountry(countryToInsertIntoDb);
				}

				countryRepository.save(countryToInsertIntoDb);
				regionRepository.saveAll(regionsToInsertIntoDb);

				return ResponsePayload.builder().status(200).message("Country and regions inserted successfully.")
						.build();
			} catch (IOException e) {
				throw new IgorIoException(e.getMessage());
			}
		} else {
			return ResponsePayload.builder().status(200).message("Regions already exist.").build();
		}
	}

	@Override
	public ResponsePayload removeCountryAndItsRegions(String countryName) {
		// get the country
		Country country = countryRepository.findByName(countryName);

		// if the country is null then throw an exception saying an unexistent country cannot be removed
		if (country == null) {
			throw new IgorNoContentToRemoveException(countryName + " cannot be removed if it does not exist.");
		}

		// get the list of regions of that country
		List<Region> regions = regionRepository.findAllByCountry(country);

		// remove all constraints from each user and then the regions and country
		secondaryRegionsUsersRepository.deleteSecondaryRegionsInList(regions.stream().map(Region::getId).toList());
		userRepository.updateCountryAndPrimaryRegionToNullByCountryIdAndRegionIds(country.getId(),
				regions.stream().map(Region::getId).toList());

		regionRepository.deleteAll(regions);
		countryRepository.delete(country);

		return ResponsePayload.builder().status(200).message("Country and regions removed successfully.").build();
	}

}
