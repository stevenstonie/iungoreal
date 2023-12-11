package com.stevenst.app.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class JwtServiceImplTest {
	/* TODO: need to figure out how to test this
	currently cannot be tested as the JwtServiceImpl uses the config file for the secret key and when 
	calling methods which use the secretKey (which will be null) the test will return an exception
	 */
	@Test
	void test() {
		assertEquals(3, 1 + 2);
	}
}
