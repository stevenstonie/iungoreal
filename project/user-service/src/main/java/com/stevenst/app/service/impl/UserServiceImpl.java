package com.stevenst.app.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.stevenst.lib.exception.IgorImageNotFoundException;
import com.stevenst.lib.exception.IgorIoException;
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
import software.amazon.awssdk.regions.Region;
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
	private final UserRepository userRepository;
	private final S3Client s3Client;
	@Value("${aws.bucketName}")
	private String bucketName;

	@Override
	public UserPublicPayload getUserPublicByUsername(String username) {
		return userRepository.findByUsername(username)
				.map(user -> UserPublicPayload.builder()
						.username(user.getUsername())
						.createdAt(user.getCreatedAt())
						.build())
				.orElseThrow(
						() -> new IgorUserNotFoundException(USER_NOT_FOUND + " (with username: " + username + ")"));
	}

	@Override
	public UserPrivatePayload getUserPrivateByUsername(String username) {
		return userRepository.findByUsername(username)
				.map(user -> UserPrivatePayload.builder()
						.id(user.getId())
						.email(user.getEmail())
						.username(user.getUsername())
						.role(user.getRole())
						.createdAt(user.getCreatedAt())
						.build())
				.orElseThrow(
						() -> new IgorUserNotFoundException(USER_NOT_FOUND + " (with username: " + username + ")"));
	}
	// TODO: users dont need to know their role is of a USER so update the code on front and back to return the role only to admins

	@Override
	public UserPrivatePayload getUserByEmail(String email) {
		return userRepository.findByEmail(email)
				.map(user -> UserPrivatePayload.builder()
						.id(user.getId())
						.email(user.getEmail())
						.username(user.getUsername())
						.role(user.getRole())
						.createdAt(user.getCreatedAt())
						.build())
				.orElseThrow(() -> new IgorUserNotFoundException(USER_NOT_FOUND + " (with email: " + email + ")"));
	}

	@Override
	public ResponsePayload savePfp(String username, MultipartFile file) {
		User user = userRepository.findByUsername(username).orElseThrow(
				() -> new IgorUserNotFoundException(USER_NOT_FOUND + " (with username: " + username + ")"));
		String fileName = file.getOriginalFilename();

		removePfp(username);

		setPfpNameInDb(user, fileName);
		return uploadPfpToS3(username, file);
	}

	@Override
	public String getPfpPreSignedLinkFromS3(String username) {
		User user = userRepository.findByUsername(username).orElseThrow(
				() -> new IgorUserNotFoundException(USER_NOT_FOUND + " (with username: " + username + ")"));

		String pfpNameFromDb = user.getProfilePictureName();
		if (pfpNameFromDb == null || pfpNameFromDb == "") {
			return JsonUtil.convertStringToJson("");
		}

		try{
			checkIfPfpExistsInS3(username, pfpNameFromDb);
		} catch (IgorImageNotFoundException e) {
			throw new IgorImageNotFoundException("No stored profile picture found for user: " + username + ".");
		}

		return JsonUtil.convertStringToJson(generatePresignedUrl(username, pfpNameFromDb));
	}

	@Override
	public ResponsePayload removePfp(String username) {
		User user = userRepository.findByUsername(username).orElseThrow(
				() -> new IgorUserNotFoundException(USER_NOT_FOUND + " (with username: " + username + ")"));

		String pfpNameFromDb = user.getProfilePictureName();
		if (pfpNameFromDb == null || pfpNameFromDb == "") {
			System.out.println("No profile picture found for user: " + username);
			return ResponsePayload.builder()
					.status(404)
					.message("No profile picture found for user: " + username)
					.build();
		}

		setPfpNameInDb(user, null);
		return removePfpFromS3(username, pfpNameFromDb);
	}

	// ----------------------------------------------------------------------------------------------------------

	private void setPfpNameInDb(User user, String fileName) {
		user.setProfilePictureName(fileName);
		userRepository.save(user);
	}

	private ResponsePayload removePfpFromS3(String username, String pfpNameFromDb) {
		DeleteObjectRequest deleteObjectRequest = createDeleteObjectRequest(username, pfpNameFromDb);
		s3Client.deleteObject(deleteObjectRequest);

		System.out.println("Removed profile picture for user: " + username);
		return ResponsePayload.builder()
				.status(200)
				.message("Removed profile picture for user: " + username)
				.build();
	}

	private ResponsePayload uploadPfpToS3(String username, MultipartFile file) {
		PutObjectRequest putObjectRequest = createPutObjectRequest(username, file.getOriginalFilename());

		try (InputStream inputStream = file.getInputStream()) {
			s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, file.getSize()));

			return ResponsePayload.builder()
					.status(200)
					.message("File uploaded to S3 bucket successfully")
					.build();
		} catch (IOException e) {
			System.err.println("Unable to convert MultipartFile to InputStream: " + e.getMessage());
			throw new IgorIoException(e.getMessage());
		}
	}

	private String generatePresignedUrl(String username, String pfpNameFromDb) {
		GetObjectRequest getObjectRequest = createGetObjectRequest(username, pfpNameFromDb);
		Duration expiration = Duration.ofHours(1);

		try (S3Presigner presigner = S3Presigner.builder().region(Region.EU_CENTRAL_1).build()) {
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

	private void checkIfPfpExistsInS3(String username, String pfpNameFromDb) {
		// TODO: make it check for the object in its path
		HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
				.bucket(bucketName)
				.key(pfpNameFromDb)
				.build();

		try {
			s3Client.headObject(headObjectRequest);
		} catch (NoSuchKeyException e) {
			throw new IgorImageNotFoundException(e.getMessage());
		} catch (S3Exception e) {
			throw new IgorIoException(e.getMessage());
		}
	}

	private PutObjectRequest createPutObjectRequest(String username, String fileName) {
		String key = username + "/profile_picture/" + fileName;
		// TODO: add metadata
		return PutObjectRequest.builder()
				.bucket(bucketName)
				.key(key)
				.build();
	}

	private GetObjectRequest createGetObjectRequest(String username, String fileName) {
		String key = username + "/profile_picture/" + fileName;

		return GetObjectRequest.builder()
				.bucket(bucketName)
				.key(key)
				.build();
	}

	private DeleteObjectRequest createDeleteObjectRequest(String username, String fileName) {
		String key = username + "/profile_picture/" + fileName;

		return DeleteObjectRequest.builder()
				.bucket(bucketName)
				.key(key)
				.build();
	}
}
