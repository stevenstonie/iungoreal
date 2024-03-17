package com.stevenst.app.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.stevenst.lib.exception.IgorCountryAndRegionException;
import com.stevenst.lib.exception.IgorEntityNotFoundException;
import com.stevenst.lib.exception.IgorImageNotFoundException;
import com.stevenst.lib.exception.IgorIoException;
import com.stevenst.lib.exception.IgorMaxCapExceededException;
import com.stevenst.lib.exception.IgorNoContentToRemoveException;
import com.stevenst.lib.exception.IgorNullValueException;
import com.stevenst.lib.exception.IgorUserNotFoundException;
import com.stevenst.lib.model.Country;
import com.stevenst.lib.model.Region;
import com.stevenst.lib.model.SecondaryRegionsUsers;
import com.stevenst.lib.model.User;
import com.stevenst.lib.payload.ResponsePayload;
import com.stevenst.app.payload.CountryOrRegionPayload;
import com.stevenst.app.payload.UserPrivatePayload;
import com.stevenst.app.payload.UserPublicPayload;
import com.stevenst.app.repository.CountryRepository;
import com.stevenst.app.repository.RegionRepository;
import com.stevenst.app.repository.SecondaryRegionsUsersRepository;
import com.stevenst.app.repository.UserRepository;
import com.stevenst.app.service.UserService;
import com.stevenst.app.util.JsonUtil;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private static final String USER_NOT_FOUND = "User not found";
	private static final String USERS_PATH = "users/";
	private static final String DEFAULTS_PATH = "defaults/";
	private static final String DEFAULT_PFP_NAME = "default-profile-picture.jpg";
	private static final int SECONDARY_REGIONS_CAP = 3;
	private final UserRepository userRepository;
	private final RegionRepository regionRepository;
	private final CountryRepository countryRepository;
	private final SecondaryRegionsUsersRepository secondaryRegionsUsersRepository;
	private final S3Client s3Client;
	@Value("${aws.bucketName}")
	private String bucketName;

	@Override
	public UserPublicPayload getUserPublicByUsername(String username) {
		User user = getUserFromDbByUsername(username);

		return UserPublicPayload.builder()
				.username(user.getUsername())
				.createdAt(user.getCreatedAt())
				.build();
	}

	@Override
	public UserPrivatePayload getUserPrivateByUsername(String username) {
		User user = getUserFromDbByUsername(username);

		return UserPrivatePayload.builder()
				.id(user.getId())
				.email(user.getEmail())
				.username(user.getUsername())
				.role(user.getRole())
				.createdAt(user.getCreatedAt())
				.build();
	}
	// TODO: users dont need to know their role is of a USER so update the code on front and back to return the role only to admins

	@Override
	public UserPrivatePayload getUserByEmail(String email) {
		User user = getUserFromDbByEmail(email);

		return UserPrivatePayload.builder()
				.id(user.getId())
				.email(user.getEmail())
				.username(user.getUsername())
				.role(user.getRole())
				.createdAt(user.getCreatedAt())
				.build();
	}

	@Override
	public ResponsePayload savePfp(String username, MultipartFile file) {
		User user = getUserFromDbByUsername(username);
		String fileName = file.getOriginalFilename();

		removePfpFromDbAndCloud(username);

		setPfpNameInDb(user, fileName);

		Map<String, String> metadata = new HashMap<>();
		metadata.put("Content-Type", file.getContentType());
		metadata.put("Content-Length", String.valueOf(file.getSize()));
		metadata.put("username", username);
		metadata.put("pfp_name", file.getOriginalFilename());
		String key = USERS_PATH + username + "/" + fileName;
		return uploadPfpToS3(key, metadata, file);
	}

	@Override
	public String getPfpPreSignedLinkFromS3(String username) {
		User user = getUserFromDbByUsername(username);

		String pfpNameFromDb = user.getProfilePictureName();
		String key = USERS_PATH + username + "/" + pfpNameFromDb;
		String MSG_FOR_EXCEPTION = "No profile picture was found stored in cloud for user: ";

		if (pfpNameFromDb == null || pfpNameFromDb.equals("")) {
			key = DEFAULTS_PATH + DEFAULT_PFP_NAME;
			MSG_FOR_EXCEPTION = MSG_FOR_EXCEPTION.replace("No profile", "No default profile");
		}

		try {
			checkIfPfpExistsInS3(key);
		} catch (IgorImageNotFoundException e) {
			throw new IgorImageNotFoundException(MSG_FOR_EXCEPTION + username + ".");
		}

		return JsonUtil.convertStringToJson(generatePresignedUrl(key));
	}

	@Override
	public ResponsePayload removePfpFromDbAndCloud(String username) {
		User user = getUserFromDbByUsername(username);

		String pfpNameFromDb = user.getProfilePictureName();
		String key = USERS_PATH + username + "/" + pfpNameFromDb;

		if (pfpNameFromDb == null || pfpNameFromDb.equals("")) {
			System.out.println("No profile picture found for user: " + username);
			return ResponsePayload.builder()
					.status(404)
					.message("No profile picture found for user: " + username)
					.build();
		}

		setPfpNameInDb(user, null);
		return removePfpFromCloud(key, username);
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

		Country country = countryRepository.findById(countryId)
				.orElseThrow(() -> new IgorEntityNotFoundException("Country not found (with id: " + countryId + ")"));

		user.setCountryId(country.getId());
		userRepository.save(user);

		return ResponsePayload.builder()
				.status(200)
				.message("Successfully set country: " + country.getName() + " for user:  " + username + ".")
				.build();
	}

	@Override
	public ResponsePayload setPrimaryRegionOfUser(String username, Long regionId) {
		User user = getUserFromDbByUsername(username);

		checkIfUserHasACountryAssingedForRegion(user);

		Region region = getRegionFromDb(regionId);

		checkIfRegionIsPartOfUsersCountry(user, region);

		checkIfRegionAlreadyExistsAsSecondary(user, region);

		user.setPrimaryRegionId(region.getId());
		userRepository.save(user);

		return ResponsePayload.builder()
				.status(200)
				.message("Successfully set primary region: " + region.getName() + " for user: " + username + ".")
				.build();
	}

	@Override
	public ResponsePayload addSecondaryRegionForUser(String username, Long regionId) {
		User user = getUserFromDbByUsername(username);
		checkIfUserHasACountryAssingedForRegion(user);

		Region regionToAddAsSecondaryInDb = getRegionFromDb(regionId);
		checkIfRegionIsPartOfUsersCountry(user, regionToAddAsSecondaryInDb);

		if (secondaryRegionsUsersRepository.countByUserId(user.getId()) >= SECONDARY_REGIONS_CAP) {
			throw new IgorMaxCapExceededException(
					"Max cap of " + SECONDARY_REGIONS_CAP + " secondary regions exceeded (for user: "
							+ user.getUsername() + ").");
		}

		checkIfRegionAlreadyExistsAsSecondary(user, regionToAddAsSecondaryInDb);

		if (Objects.equals(regionToAddAsSecondaryInDb.getId(), user.getPrimaryRegionId())) {
			throw new IgorCountryAndRegionException(
					"Cannot assign this region because it is already primary (for user: " + user.getUsername() + ").");
		}

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

		user.setCountryId(null);
		user.setPrimaryRegionId(null);
		userRepository.save(user);

		secondaryRegionsUsersRepository.removeAllByUserId(user.getId());

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

		SecondaryRegionsUsers secondaryRegion = secondaryRegionsUsersRepository
				.findByUserIdAndSecondaryRegionId(user.getId(), regionId);
		if (secondaryRegion == null) {
			throw new IgorNoContentToRemoveException(
					"Secondary region to remove was not found (for user: " + username + ").");
		}

		secondaryRegionsUsersRepository.delete(secondaryRegion);
		return ResponsePayload.builder()
				.status(200)
				.message("Removed secondary region for user: " + username + ".")
				.build();
	}

	// ----------------------------------------------------------------------------------------------------------

	private User getUserFromDbByUsername(String username) {
		if (username == null || username.equals("")) {
			throw new IgorNullValueException("Username cannot be null or empty.");
		}

		return userRepository.findByUsername(username)
				.orElseThrow(
						() -> new IgorUserNotFoundException(USER_NOT_FOUND + " (with username: " + username + ")."));
	}

	private User getUserFromDbByEmail(String email) {
		if (email == null || email.equals("")) {
			throw new IgorNullValueException("Email cannot be null or empty.");
		}

		return userRepository.findByEmail(email)
				.orElseThrow(
						() -> new IgorUserNotFoundException(USER_NOT_FOUND + " (with email: " + email + ")."));
	}

	private Region getRegionFromDb(Long regionId) {
		if (regionId == null) {
			throw new IgorNullValueException("Region id cannot be null.");
		}

		return regionRepository.findById(regionId)
				.orElseThrow(() -> new IgorEntityNotFoundException("Region not found (with id: " + regionId + ")."));
	}

	private Country getCountryFromDb(Long countryId) {
		if (countryId == null) {
			throw new IgorNullValueException("Country id cannot be null.");
		}

		return countryRepository.findById(countryId)
				.orElseThrow(() -> new IgorEntityNotFoundException("Country not found (with id: " + countryId + ")."));
	}

	private void checkIfUserHasACountryAssingedForRegion(User user) {
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

	private void setPfpNameInDb(User user, String fileName) {
		user.setProfilePictureName(fileName);
		userRepository.save(user);
	}

	private ResponsePayload removePfpFromCloud(String key, String username) {
		DeleteObjectRequest deleteObjectRequest = createDeleteObjectRequest(key);
		s3Client.deleteObject(deleteObjectRequest);

		System.out.println("Removed profile picture for user: " + username);
		return ResponsePayload.builder()
				.status(200)
				.message("Removed profile picture for user: " + username)
				.build();
	}

	private ResponsePayload uploadPfpToS3(String key, Map<String, String> metadata, MultipartFile file) {
		PutObjectRequest putObjectRequest = createPutObjectRequest(key, metadata);
		try (InputStream inputStream = file.getInputStream()) {
			s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, file.getSize()));

			return ResponsePayload.builder()
					.status(200)
					.message("Profile picture uploaded successfully.")
					.build();
		} catch (IOException e) {
			System.err.println("Unable to convert MultipartFile to InputStream: " + e.getMessage());
			throw new IgorIoException(e.getMessage());
		}
	}

	private String generatePresignedUrl(String key) {
		GetObjectRequest getObjectRequest = createGetObjectRequest(key);
		Duration expiration = Duration.ofHours(1);

		try (S3Presigner presigner = S3Presigner.builder().region(software.amazon.awssdk.regions.Region.EU_CENTRAL_1)
				.build()) {
			GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
					.getObjectRequest(getObjectRequest)
					.signatureDuration(expiration)
					.build();

			PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);

			return presignedRequest.url().toString();
		} catch (S3Exception e) {
			System.err.println("Unable to generate a presigned url:" + e.getMessage());
			throw new IgorIoException(e.getMessage());
		}
	}

	private void checkIfPfpExistsInS3(String key) {
		HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
				.bucket(bucketName)
				.key(key)
				.build();

		try {
			s3Client.headObject(headObjectRequest);
		} catch (NoSuchKeyException e) {
			throw new IgorImageNotFoundException(e.getMessage());
		} catch (S3Exception e) {
			throw new IgorIoException(e.getMessage());
		}
	}

	private PutObjectRequest createPutObjectRequest(String key, Map<String, String> metadata) {
		return PutObjectRequest.builder()
				.bucket(bucketName)
				.key(key)
				.metadata(metadata)
				.build();
	}

	private GetObjectRequest createGetObjectRequest(String key) {
		return GetObjectRequest.builder()
				.bucket(bucketName)
				.key(key)
				.build();
	}

	private DeleteObjectRequest createDeleteObjectRequest(String key) {
		return DeleteObjectRequest.builder()
				.bucket(bucketName)
				.key(key)
				.build();
	}
}
