package com.stevenst.app.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.stevenst.lib.model.enums.Role;
import com.stevenst.lib.model.User;

class UserTest {
	@Test
	void testFinalFields() {
		User user = new User();

		assertNotNull(user.getCreatedAt());
		assertEquals(Role.USER, user.getRole());
	}
}
