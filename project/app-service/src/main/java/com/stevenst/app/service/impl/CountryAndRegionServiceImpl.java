package com.stevenst.app.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.stevenst.app.model.chat.Chatroom;
import com.stevenst.app.model.chat.ChatroomType;
import com.stevenst.app.model.chat.ChatroomsRegions;
import com.stevenst.app.repository.ChatroomRepository;
import com.stevenst.app.repository.ChatroomsRegionsRepository;
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
	private final UserRepository userRepository;
	private final RegionRepository regionRepository;
	private final CountryRepository countryRepository;
	private final SecondaryRegionsUsersRepository secondaryRegionsUsersRepository;
	private final ChatroomRepository chatroomRepository;
	private final ChatroomsRegionsRepository chatroomsRegionsRepository;

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

				// create chatrooms for these regions and assign them to the junction table
				createChatroomsForRegions(regionsToInsertIntoDb);

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

		// first remove all chatrooms of these regions by junction table
		removeAllChatroomsForRegions(regions);

		// then remove all constraints from each user and the regions and country
		userRepository.updateCountryAndPrimaryRegionToNullByCountryIdAndRegionIds(country.getId(),
				regions.stream().map(Region::getId).toList());
		secondaryRegionsUsersRepository.deleteSecondaryRegionsInList(regions.stream().map(Region::getId).toList());

		// and lastly remove the regions and country themselves
		regionRepository.deleteAll(regions);
		countryRepository.delete(country);

		return ResponsePayload.builder().status(200).message("Country and regions removed successfully.").build();
	}

	// ---------------------------------------------------------------------------------

	private void createChatroomsForRegions(List<Region> addedRegions) {
		// for each region create a chatroom and add an entry in the junction table to connect them
		for (Region region : addedRegions) {
			Chatroom chatroom = Chatroom.builder().name(region.getName()).type(ChatroomType.REGIONAL).build();
			chatroom = chatroomRepository.save(chatroom);

			ChatroomsRegions chatroomsRegions = ChatroomsRegions.builder().regionId(region.getId())
					.chatroomId(chatroom.getId()).build();
			chatroomsRegionsRepository.save(chatroomsRegions);
		}
	}

	private void removeAllChatroomsForRegions(List<Region> regionsToBeDeleted) {
		// for each region remove the chatroom associated with it by the junction table
		for (Region region : regionsToBeDeleted) {
			// get the id of the chatroom and then remove both the constraint and the chatroom with it
			Long chatroomId = chatroomsRegionsRepository.findChatroomIdByRegionId(region.getId());
			chatroomsRegionsRepository.deleteByChatroomId(chatroomId);

			chatroomRepository.deleteById(chatroomId);
		}
	}

}
