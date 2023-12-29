package com.stevenst.app.model;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.stevenst.lib.model.User;

class UserTest {
	
	@Test
	void testCreatedAtIsNotNull() {
		User user = new User();
		assertNotNull(user.getCreatedAt());
	}
}
