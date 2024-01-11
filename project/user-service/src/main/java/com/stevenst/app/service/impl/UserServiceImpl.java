package com.stevenst.app.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.stevenst.lib.exception.IgorEmptyFileNameException;
import com.stevenst.lib.exception.IgorIoException;
import com.stevenst.lib.exception.IgorUserNotFoundException;
import com.stevenst.app.payload.UserPrivatePayload;
import com.stevenst.app.payload.UserPublicPayload;
import com.stevenst.app.repository.UserRepository;
import com.stevenst.app.service.UserService;
import com.stevenst.lib.model.User;
import com.stevenst.lib.payload.ResponsePayload;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	private static final String USER_NOT_FOUND = "User not found";
	private final UserRepository userRepository;
	@Value("${app.media-path}")
	private String mediaPath;

	@Override
	public UserPublicPayload getUserPublicByUsername(String username) {
		return userRepository.findByUsername(username)
				.map(user -> UserPublicPayload.builder()
						.username(user.getUsername())
						.createdAt(user.getCreatedAt())
						.build())
				.orElseThrow(() -> new IgorUserNotFoundException(USER_NOT_FOUND + " (with username: " + username + ")"));
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
				.orElseThrow(() -> new IgorUserNotFoundException(USER_NOT_FOUND + " (with username: " + username + ")"));
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
	public ResponsePayload saveProfilePicture(String username, MultipartFile file) {
		User user = userRepository.findByUsername(username)
				.orElseThrow(() -> new IgorUserNotFoundException(USER_NOT_FOUND + " (with username: " + username + ")"));

		String fileName = storeProfilePictureAndReturnFileName(username, file);

		user.setProfilePictureName(fileName);
		userRepository.save(user);

		return ResponsePayload.builder()
				.status(200)
				.message("Profile picture stored successfully")
				.build();
	}

	private String storeProfilePictureAndReturnFileName(String username, MultipartFile file) {
		try {
			String originalFileName = file.getOriginalFilename();
			if (originalFileName == null) {
				throw new IgorEmptyFileNameException("Cannot store a profile picture with a null name");
			}

			Path targetDirectory = Paths.get(mediaPath, username, "profile_picture");
			deleteFolderIfExists(targetDirectory);

			Files.createDirectories(targetDirectory);

			Path targetPath = targetDirectory.resolve(originalFileName);

			Files.write(targetPath, file.getBytes(), StandardOpenOption.CREATE_NEW);

			return targetPath.getFileName().toString();
		} catch (IOException e) {
			throw new IgorIoException("Could not store file");
		}

	}

	private void deleteFolderIfExists(Path path) {
		if (Files.exists(path)) {
			try (Stream<Path> walk = Files.walk(path)) {
				walk.sorted(Path::compareTo).map(Path::toFile).forEach(File::delete);
				Files.deleteIfExists(path);
			} catch (NoSuchFileException e) {
				System.err.printf("Failed to delete non-existent directory %s%n", path);
			} catch (DirectoryNotEmptyException e) {
				System.err.printf("Failed to delete non-empty directory %s%n", path);
			} catch (IOException e) {
				System.err.printf("I/O Error when deleting directory %s%n", path);
			}
		}
	}
}
