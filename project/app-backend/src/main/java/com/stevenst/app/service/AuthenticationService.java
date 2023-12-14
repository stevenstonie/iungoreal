package com.stevenst.app.service;

import com.stevenst.app.auth.AuthenticationRequest;
import com.stevenst.app.auth.AuthenticationResponse;
import com.stevenst.app.auth.RegisterRequest;

public interface AuthenticationService {
	AuthenticationResponse register(RegisterRequest request);

	AuthenticationResponse login(AuthenticationRequest request);
}
