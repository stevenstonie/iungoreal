package com.stevenst.app.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.stevenst.app.repository.ChatroomParticipantRepository;
import com.stevenst.app.repository.ChatroomRepository;
import com.stevenst.app.repository.ChatroomsRegionsRepository;
import com.stevenst.app.repository.CountryRepository;
import com.stevenst.app.repository.RegionRepository;
import com.stevenst.app.repository.SecondaryRegionsUsersRepository;
import com.stevenst.app.repository.UserRepository;
import com.stevenst.app.service.CountryAndRegionService;
import com.stevenst.lib.exception.IgorCountryAndRegionException;
import com.stevenst.lib.exception.IgorEntityNotFoundException;
import com.stevenst.lib.exception.IgorMaxCapExceededException;
import com.stevenst.lib.exception.IgorNoContentToRemoveException;
import com.stevenst.lib.exception.IgorNullValueException;
import com.stevenst.lib.exception.IgorUserNotFoundException;
import com.stevenst.lib.model.Country;
import com.stevenst.lib.model.Region;
import com.stevenst.lib.model.SecondaryRegionsUsers;
import com.stevenst.lib.model.User;
import com.stevenst.lib.model.chat.Chatroom;
import com.stevenst.lib.model.chat.ChatroomParticipant;
import com.stevenst.lib.payload.CountryOrRegionPayload;
import com.stevenst.lib.payload.ResponsePayload;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CountryAndRegionServiceImpl implements CountryAndRegionService {
	private static final int SECONDARY_REGIONS_CAP = 3;
	private final UserRepository userRepository;
	private final RegionRepository regionRepository;
	private final CountryRepository countryRepository;
	private final SecondaryRegionsUsersRepository secondaryRegionsUsersRepository;
	private final ChatroomRepository chatroomRepository;
	private final ChatroomParticipantRepository chatroomParticipantRepository;
	private final ChatroomsRegionsRepository chatroomsRegionsRepository;

	@Override
	public List<CountryOrRegionPayload> getAvailableRegionsForUser(String username) {
		User user = getUserFromDbByUsername(username);
		if (user.getCountryId() == null) {
			return new ArrayList<>();
		}
		Country country = getCountryFromDb(user.getCountryId());

		List<Long> regionIdsToExclude = getRegionIdsToExclude(user);

		List<Region> regions = new ArrayList<>();
		if (regionIdsToExclude.isEmpty()) {
			regions = regionRepository.findAllByCountry(country);
		} else {
			regions = regionRepository.findAllByCountryAndIdNotIn(country, regionIdsToExclude);
		}

		regions.sort(Comparator.comparing(Region::getName));

		return regions.stream()
				.map(region -> CountryOrRegionPayload.builder()
						.id(region.getId())
						.name(region.getName())
						.build())
				.collect(Collectors.toList());
	}

	@Override
	public CountryOrRegionPayload getCountryOfUser(String username) {
		User user = getUserFromDbByUsername(username);

		if (user.getCountryId() == null) {
			return new CountryOrRegionPayload(null, null);
		}

		Country country = getCountryFromDb(user.getCountryId());

		return CountryOrRegionPayload.builder()
				.id(country.getId())
				.name(country.getName())
				.build();
	}

	@Override
	public CountryOrRegionPayload getPrimaryRegionOfUser(String username) {
		User user = getUserFromDbByUsername(username);

		if (user.getPrimaryRegionId() == null) {
			return new CountryOrRegionPayload(null, null);
		}

		Region region = getRegionFromDb(user.getPrimaryRegionId());

		return CountryOrRegionPayload.builder()
				.id(region.getId())
				.name(region.getName())
				.build();
	}

	@Override
	public List<CountryOrRegionPayload> getSecondaryRegionsOfUser(String username) {
		User user = getUserFromDbByUsername(username);

		List<SecondaryRegionsUsers> secondaryRegionsOfUser = secondaryRegionsUsersRepository.findByUserId(user.getId());

		List<CountryOrRegionPayload> secondaryRegions = new java.util.ArrayList<>();

		for (SecondaryRegionsUsers secondaryRegionOfUser : secondaryRegionsOfUser) {
			secondaryRegions.add(CountryOrRegionPayload.builder()
					.id(secondaryRegionOfUser.getSecondaryRegion().getId())
					.name(secondaryRegionOfUser.getSecondaryRegion().getName())
					.build());
		}

		return secondaryRegions;
	}

	@Override
	public ResponsePayload setCountryForUser(String username, Long countryId) {
		User user = getUserFromDbByUsername(username);

		Country country = getCountryFromDb(countryId);

		if (!Objects.equals(country.getId(), user.getCountryId())) {
			user.setPrimaryRegionId(null);
			secondaryRegionsUsersRepository.removeAllByUserId(user.getId());
		}

		user.setCountryId(country.getId());
		userRepository.save(user);

		return ResponsePayload.builder()
				.status(200)
				.message("Successfully set country: " + country.getName() + " for user: " + username + ".")
				.build();
	}

	@Override
	public ResponsePayload setPrimaryRegionOfUser(String username, Long regionId) {
		User user = getUserFromDbByUsername(username);

		checkIfUserHasACountryAssigned(user);

		Region regionToSetAsPrimary = getRegionFromDb(regionId);

		checkIfRegionIsPartOfUsersCountry(user, regionToSetAsPrimary);

		checkIfRegionAlreadyExistsAsSecondary(user, regionToSetAsPrimary);

		if (Objects.equals(regionToSetAsPrimary.getId(), user.getPrimaryRegionId())) {
			throw new IgorCountryAndRegionException(
					"Cannot assign this region because it is already primary (for user: " + user.getUsername() + ").");
		}

		addUserAsParticipantForTheChatroomOfThisRegion(user, regionToSetAsPrimary);

		user.setPrimaryRegionId(regionToSetAsPrimary.getId());
		userRepository.save(user);

		return ResponsePayload.builder()
				.status(200)
				.message("Successfully set primary region: " + regionToSetAsPrimary.getName() + " for user: " + username
						+ ".")
				.build();
	}

	@Override
	public ResponsePayload addSecondaryRegionForUser(String username, Long regionId) {
		User user = getUserFromDbByUsername(username);
		checkIfUserHasACountryAssigned(user);

		Region regionToAddAsSecondaryInDb = getRegionFromDb(regionId);
		checkIfRegionIsPartOfUsersCountry(user, regionToAddAsSecondaryInDb);

		if (secondaryRegionsUsersRepository.countByUserId(user.getId()) >= SECONDARY_REGIONS_CAP) {
			throw new IgorMaxCapExceededException(
					"Max cap of " + SECONDARY_REGIONS_CAP + " secondary regions exceeded (for user: "
							+ user.getUsername() + ").");
		}

		if (Objects.equals(regionToAddAsSecondaryInDb.getId(), user.getPrimaryRegionId())) {
			throw new IgorCountryAndRegionException(
					"Cannot assign this region because it is already primary (for user: " + user.getUsername() + ").");
		}

		checkIfRegionAlreadyExistsAsSecondary(user, regionToAddAsSecondaryInDb);

		addUserAsParticipantForTheChatroomOfThisRegion(user, regionToAddAsSecondaryInDb);

		secondaryRegionsUsersRepository.save(
				Objects.requireNonNull(SecondaryRegionsUsers.builder()
						.user(user)
						.secondaryRegion(regionToAddAsSecondaryInDb)
						.build()));

		return ResponsePayload.builder()
				.status(200)
				.message("Successfully added a secondary region: " + regionToAddAsSecondaryInDb.getName()
						+ " for user: " + username + ".")
				.build();
	}

	@Override
	public ResponsePayload removeCountryForUser(String username) {
		User user = getUserFromDbByUsername(username);

		if (user.getCountryId() == null) {
			throw new IgorNoContentToRemoveException("No country to remove was found (for user: " + username + ").");
		}

		// if the user has a primary region search if he is participant in the chatroom assigned to that region and remove if found
		if (user.getPrimaryRegionId() != null) {
			removeUserAsParticipantForTheChatroomOfTheRegionId(user, user.getPrimaryRegionId());
		}

		// go through the secondary regions of his and do the same as above with each one
		List<SecondaryRegionsUsers> secondaryRegionJoiners = secondaryRegionsUsersRepository.findByUserId(user.getId());
		for (SecondaryRegionsUsers secondaryRegionJoiner : secondaryRegionJoiners) {
			removeUserAsParticipantForTheChatroomOfTheRegionId(user,
					secondaryRegionJoiner.getSecondaryRegion().getId());
		}

		secondaryRegionsUsersRepository.removeAllByUserId(user.getId());
		user.setCountryId(null);
		user.setPrimaryRegionId(null);
		userRepository.save(user);

		return ResponsePayload.builder()
				.status(200)
				.message("Successfully removed country and regions for user: " + username + ".")
				.build();
	}

	@Override
	public ResponsePayload removePrimaryRegionForUser(String username) {
		User user = getUserFromDbByUsername(username);
		if (user.getPrimaryRegionId() == null) {
			throw new IgorNoContentToRemoveException(
					"No primary region to remove was found (for user: " + username + ").");
		}

		// remove the user as participant in the chatroom assigned to the region's id
		removeUserAsParticipantForTheChatroomOfTheRegionId(user, user.getPrimaryRegionId());

		user.setPrimaryRegionId(null);
		userRepository.save(user);
		return ResponsePayload.builder()
				.status(200)
				.message("Removed primary region for user: " + username + ".")
				.build();
	}

	@Override
	public ResponsePayload removeSecondaryRegionForUser(String username, Long regionId) {
		User user = getUserFromDbByUsername(username);
		SecondaryRegionsUsers secondaryRegionLinker = getSecondaryRegionFromDb(user, regionId);

		removeUserAsParticipantForTheChatroomOfTheRegionId(user, secondaryRegionLinker.getSecondaryRegion().getId());

		secondaryRegionsUsersRepository.delete(secondaryRegionLinker);
		return ResponsePayload.builder()
				.status(200)
				.message("Removed secondary region for user: " + username + ".")
				.build();
	}

	// -------------------------------------------------------------------------------------------------------------------

	private User getUserFromDbByUsername(String username) {
		if (username == null || username.equals("")) {
			throw new IgorNullValueException("Username cannot be null or empty.");
		}

		return userRepository.findByUsername(username)
				.orElseThrow(
						() -> new IgorUserNotFoundException("User not found (with username: " + username + ")."));
	}

	private Country getCountryFromDb(Long countryId) {
		if (countryId == null) {
			throw new IgorNullValueException("Country id cannot be null.");
		}

		return countryRepository.findById(countryId)
				.orElseThrow(() -> new IgorEntityNotFoundException("Country not found (with id: " + countryId + ")."));
	}

	private void addUserAsParticipantForTheChatroomOfThisRegion(User user, Region region) {
		// find the chatroom assigned to this region
		Long idOfChatroomAssignedToTheRegion = chatroomsRegionsRepository
				.findChatroomIdByRegionId(region.getId());
		Chatroom chatroom = chatroomRepository.findById(idOfChatroomAssignedToTheRegion).get();

		// check if the participant is already in the chatroom
		ChatroomParticipant participant = chatroomParticipantRepository
				.findChatroomParticipantByChatroomAndUser(chatroom, user);
		if (participant != null) {
			return;
		}

		// if not add the user as chatroom participant for it
		chatroomParticipantRepository
				.save(ChatroomParticipant.builder().chatroom(chatroom).user(user).hasLeft(false).build());
	}

	private void removeUserAsParticipantForTheChatroomOfTheRegionId(User user, Long regionId) {
		// find the chatroom assigned to this region
		Long idOfChatroomAssignedToTheRegion = chatroomsRegionsRepository
				.findChatroomIdByRegionId(regionId);
		Chatroom chatroom = chatroomRepository.findById(idOfChatroomAssignedToTheRegion).get();

		// and remove the user as chatroom participant for it
		chatroomParticipantRepository
				.deleteByChatroomAndUser(chatroom, user);
	}

	private Region getRegionFromDb(Long regionId) {
		if (regionId == null) {
			throw new IgorNullValueException("Region id cannot be null.");
		}

		return regionRepository.findById(regionId)
				.orElseThrow(() -> new IgorEntityNotFoundException("Region not found (with id: " + regionId + ")."));
	}

	private SecondaryRegionsUsers getSecondaryRegionFromDb(User user, Long regionId) {
		if (regionId == null) {
			throw new IgorNullValueException("Region id cannot be null.");
		}

		SecondaryRegionsUsers secondaryRegion = secondaryRegionsUsersRepository
				.findByUserIdAndSecondaryRegionId(user.getId(), regionId);
		if (secondaryRegion == null) {
			throw new IgorEntityNotFoundException(
					"Region not found (with id: " + regionId + ") associated with user: " + user.getUsername() + ".");
		}

		return secondaryRegion;
	}

	private void checkIfUserHasACountryAssigned(User user) {
		if (user.getCountryId() == null) {
			throw new IgorCountryAndRegionException(
					"Cannot assign a region if country is not set (for user: " + user.getUsername() + ").");
		}
	}

	private void checkIfRegionIsPartOfUsersCountry(User user, Region region) {
		if (!region.getCountry().getId().equals(user.getCountryId())) {
			throw new IgorCountryAndRegionException(
					"Cannot assign a region of a country that is not the user's country (for user: "
							+ user.getUsername() + ").");
		}
	}

	private void checkIfRegionAlreadyExistsAsSecondary(User user, Region region) {
		List<CountryOrRegionPayload> secondaryRegions = getSecondaryRegionsOfUser(user.getUsername());

		if (secondaryRegions.stream().anyMatch(r -> r.getId().equals(region.getId()))) {
			throw new IgorCountryAndRegionException(
					"Cannot assign a region that is already a secondary region (for user: " + user.getUsername()
							+ ").");
		}
	}

	private List<Long> getRegionIdsToExclude(User user) {
		List<CountryOrRegionPayload> regionsToExclude = getSecondaryRegionsOfUser(user.getUsername());
		if (user.getPrimaryRegionId() != null) {
			regionsToExclude.add(getPrimaryRegionOfUser(user.getUsername()));
		}

		return regionsToExclude.stream()
				.map(CountryOrRegionPayload::getId)
				.collect(Collectors.toList());
	}

}
