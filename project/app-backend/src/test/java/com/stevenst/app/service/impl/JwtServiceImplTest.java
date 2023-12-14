package com.stevenst.app.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class JwtServiceImplTest {
	/* TODO: need to figure out how to test this class
	 * currently cannot be done as the JwtServiceImpl uses the config file for the secret key and 
	 * a lot of methods use 'secretKey' which is null.
	 */
	@Test
	void test() {
		assertEquals(3, 1 + 2);
	}
}
