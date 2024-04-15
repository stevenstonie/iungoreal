package com.stevenst.app.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stevenst.app.payload.UserPublicPayload;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/search")
public class SearchController {

	@GetMapping("/getUsersMatching")
	public ResponseEntity<List<UserPublicPayload>> getUsersMatching(@RequestParam("input") String input) {
		return ResponseEntity.ok(null);
	}
}
