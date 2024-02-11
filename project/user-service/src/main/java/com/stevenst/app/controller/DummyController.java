package com.stevenst.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.stevenst.app.service.AmazonS3Service;
import com.stevenst.lib.payload.ResponsePayload;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class DummyController {
	// private final _ _;
	private final AmazonS3Service amazonS3Service;

	@GetMapping("/hello")
	public String hello() {
		return "hello";
	}

	// @GetMapping("/getpfpfroms3")
	// public ResponseEntity<String> getPfpFromS3(@RequestParam String username) {
	// 	return amazonS3Service.getPfpFromS3(username);
	// }
}
