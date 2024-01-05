package com.stevenst.app.util;

import com.stevenst.app.repository.TestRepository;
import com.stevenst.lib.model.User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TestUtil {
	private final TestRepository testRepository;

	public void insertUserIntoDB(User user) {
		testRepository.save(user);
	}

	public void cleanDB() {
		testRepository.deleteAllFriendRequests();
		testRepository.deleteAllFriendships();
		testRepository.deleteAllUsers();
	}
}
