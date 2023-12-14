package com.stevenst.app.service;

import com.stevenst.app.auth.AuthRequest;
import com.stevenst.app.auth.AuthResponse;
import com.stevenst.app.auth.RegisterRequest;

public interface AuthenticationService {
	AuthResponse register(RegisterRequest request);

	AuthResponse login(AuthRequest request);
}
