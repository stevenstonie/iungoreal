package com.stevenst.app.model;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class UserTest {
	
	@Test
	void testCreatedAtIsNotNull() {
		User user = new User();
		assertNotNull(user.getCreatedAt());
	}
}
