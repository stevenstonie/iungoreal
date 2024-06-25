package com.stevenst.app.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.stevenst.lib.exception.IgorImageNotFoundException;
import com.stevenst.lib.exception.IgorIoException;
import com.stevenst.lib.exception.IgorNullValueException;
import com.stevenst.lib.exception.IgorUserNotFoundException;
import com.stevenst.lib.model.User;
import com.stevenst.lib.payload.ResponsePayload;
import com.stevenst.app.payload.UserPrivatePayload;
import com.stevenst.app.payload.UserPublicPayload;
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
	private static final String DEFAULT_COVER_IMG_NAME = "default-cover-photo.jpg";
	private final UserRepository userRepository;
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
			checkIfObjectExistsInS3(key);
		} catch (IgorImageNotFoundException e) {
			throw new IgorImageNotFoundException(MSG_FOR_EXCEPTION + username + ".");
		}

		return JsonUtil.convertStringToJson(generatePresignedUrl(key));
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
	public String getCoverImgPresignedLinkFromS3(String username) {
		User user = getUserFromDbByUsername(username);

		String coverNameFromDb = user.getCoverImageName();
		String key = USERS_PATH + username + "/cover/" + coverNameFromDb;
		String MSG_FOR_EXCEPTION = "No cover image was found stored in cloud for user: ";

		if (coverNameFromDb == null || coverNameFromDb.equals("")) {
			key = DEFAULTS_PATH + DEFAULT_COVER_IMG_NAME;
			MSG_FOR_EXCEPTION = MSG_FOR_EXCEPTION.replace("No cover", "No default cover");
		}

		try {
			checkIfObjectExistsInS3(key);
		} catch (IgorImageNotFoundException e) {
			throw new IgorImageNotFoundException(MSG_FOR_EXCEPTION + username + ".");
		}

		return JsonUtil.convertStringToJson(generatePresignedUrl(key));
	}

	@Override
	public ResponsePayload removeCoverImgFromDbAndCloud(String username) {
		User user = getUserFromDbByUsername(username);

		String coverImgFromDb = user.getCoverImageName();
		String key = USERS_PATH + username + "/cover/" + coverImgFromDb;

		if (coverImgFromDb == null || coverImgFromDb.equals("")) {
			System.out.println("No cover image found for user: " + username);
			return ResponsePayload.builder()
					.status(404)
					.message("No cover image found for user: " + username)
					.build();
		}

		setCoverImgInDb(user, null);
		return removeCoverImgFromCloud(key, username);
	}

	@Override
	public ResponsePayload saveCoverImg(String username, MultipartFile file) {
		User user = getUserFromDbByUsername(username);
		String fileName = file.getOriginalFilename();

		removeCoverImgFromDbAndCloud(username);

		setCoverImgInDb(user, fileName);

		Map<String, String> metadata = new HashMap<>();
		metadata.put("Content-Type", file.getContentType());
		metadata.put("Content-Length", String.valueOf(file.getSize()));
		metadata.put("username", username);
		metadata.put("cover_img_name", file.getOriginalFilename());
		String key = USERS_PATH + username + "/cover/" + fileName;
		return uploadCoverImgToS3(key, metadata, file);
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

	private ResponsePayload removePfpFromCloud(String key, String username) {
		DeleteObjectRequest deleteObjectRequest = createDeleteObjectRequest(key);
		s3Client.deleteObject(deleteObjectRequest);

		System.out.println("Removed profile picture for user: " + username);
		return ResponsePayload.builder()
				.status(200)
				.message("Removed profile picture for user: " + username)
				.build();
	}

	private ResponsePayload removeCoverImgFromCloud(String key, String username) {
		DeleteObjectRequest deleteObjectRequest = createDeleteObjectRequest(key);
		s3Client.deleteObject(deleteObjectRequest);

		System.out.println("Removed cover image for user: " + username);
		return ResponsePayload.builder()
				.status(200)
				.message("Removed cover image for user: " + username)
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

	private ResponsePayload uploadCoverImgToS3(String key, Map<String, String> metadata, MultipartFile file) {
		PutObjectRequest putObjectRequest = createPutObjectRequest(key, metadata);
		try (InputStream inputStream = file.getInputStream()) {
			s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, file.getSize()));

			return ResponsePayload.builder()
					.status(200)
					.message("Cover image uploaded successfully.")
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

	private void setPfpNameInDb(User user, String fileName) {
		user.setProfilePictureName(fileName);
		userRepository.save(user);
	}

	private void setCoverImgInDb(User user, String fileName) {
		user.setCoverImageName(fileName);
		userRepository.save(user);
	}

	private void checkIfObjectExistsInS3(String key) {
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
