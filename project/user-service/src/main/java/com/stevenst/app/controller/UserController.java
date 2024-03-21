package com.stevenst.app.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.stevenst.app.controller.api.UserApi;
import com.stevenst.lib.payload.CountryOrRegionPayload;
import com.stevenst.app.payload.UserPrivatePayload;
import com.stevenst.app.payload.UserPublicPayload;
import com.stevenst.app.service.UserService;
import com.stevenst.lib.payload.ResponsePayload;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController implements UserApi {
	private final UserService userService;

	@GetMapping("/getPublicByUsername")
	public ResponseEntity<UserPublicPayload> getUserPublicByUsername(@RequestParam String username) {
		return ResponseEntity.ok(userService.getUserPublicByUsername(username));
	}

	@GetMapping("/getPrivateByUsername")
	public ResponseEntity<UserPrivatePayload> getUserPrivateByUsername(@RequestParam String username) {
		return ResponseEntity.ok(userService.getUserPrivateByUsername(username));
	}

	@GetMapping("/getByEmail")
	public ResponseEntity<UserPrivatePayload> getUserByEmail(@RequestParam String email) {
		return ResponseEntity.ok(userService.getUserByEmail(email));
	}

	// ---------------------- profile picture and cover image

	@PutMapping("/saveProfilePicture")
	public ResponseEntity<ResponsePayload> saveProfilePicture(@RequestParam String username,
			@RequestParam MultipartFile file) {
		return ResponseEntity.ok(userService.savePfp(username, file));
	}

	@GetMapping("getProfilePictureLink")
	public ResponseEntity<String> getProfilePictureLink(@RequestParam String username) {
		return ResponseEntity.ok(userService.getPfpPreSignedLinkFromS3(username));
	}

	@DeleteMapping("removeProfilePicture")
	public ResponseEntity<ResponsePayload> removePfpFromDbAndCloud(@RequestParam String username) {
		return ResponseEntity.ok(userService.removePfpFromDbAndCloud(username));
	}

	// ---------------------- countries and regions

	@GetMapping("/getAvailableRegions")
	public ResponseEntity<List<CountryOrRegionPayload>> getAvailableRegionsForUser(@RequestParam String username) {
		return ResponseEntity.ok(userService.getAvailableRegionsForUser(username));
	}

	@GetMapping("/getCountry")
	public ResponseEntity<CountryOrRegionPayload> getCountryOfUser(@RequestParam String username) {
		return ResponseEntity.ok(userService.getCountryOfUser(username));
	}

	@GetMapping("/getPrimaryRegion")
	public ResponseEntity<CountryOrRegionPayload> getPrimaryRegionOfUser(@RequestParam String username) {
		return ResponseEntity.ok(userService.getPrimaryRegionOfUser(username));
	}

	@GetMapping("/getSecondaryRegions")
	public ResponseEntity<List<CountryOrRegionPayload>> getSecondaryRegionsOfUser(@RequestParam String username) {
		return ResponseEntity.ok(userService.getSecondaryRegionsOfUser(username));
	}

	@PutMapping("/setCountry")
	public ResponseEntity<ResponsePayload> setCountryForUser(@RequestParam String username,
			@RequestParam Long countryId) {
		return ResponseEntity.ok(userService.setCountryForUser(username, countryId));
	}

	@PutMapping("/setPrimaryRegion")
	public ResponseEntity<ResponsePayload> setPrimaryRegionOfUser(@RequestParam String username,
			@RequestParam Long regionId) {
		return ResponseEntity.ok(userService.setPrimaryRegionOfUser(username, regionId));
	}

	@PostMapping("/addSecondaryRegion")
	public ResponseEntity<ResponsePayload> addSecondaryRegionForUser(@RequestParam String username,
			@RequestParam Long regionId) {
		return ResponseEntity.ok(userService.addSecondaryRegionForUser(username, regionId));
	}

	@DeleteMapping("/removeCountry")
	public ResponseEntity<ResponsePayload> removeCountryForUser(@RequestParam String username) {
		return ResponseEntity.ok(userService.removeCountryForUser(username));
	}

	@DeleteMapping("/removePrimaryRegion")
	public ResponseEntity<ResponsePayload> removePrimaryRegionForUser(@RequestParam String username) {
		return ResponseEntity.ok(userService.removePrimaryRegionForUser(username));
	}

	@DeleteMapping("/removeSecondaryRegion")
	public ResponseEntity<ResponsePayload> removeSecondaryRegionForUser(@RequestParam String username,
			@RequestParam Long regionId) {
		return ResponseEntity.ok(userService.removeSecondaryRegionForUser(username, regionId));
	}
}
