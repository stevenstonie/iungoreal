package com.stevenst.app.service;

import com.stevenst.app.payload.AuthRequest;
import com.stevenst.app.payload.AuthResponse;
import com.stevenst.app.payload.RegisterRequest;

public interface AuthenticationService {
	AuthResponse register(RegisterRequest request);

	AuthResponse login(AuthRequest request);
}
