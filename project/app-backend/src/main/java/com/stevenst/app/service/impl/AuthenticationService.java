package com.stevenst.app.service.impl;

import com.stevenst.app.auth.AuthenticationRequest;
import com.stevenst.app.auth.AuthenticationResponse;
import com.stevenst.app.auth.RegisterRequest;
import com.stevenst.app.model.Role;
import com.stevenst.app.model.User;
import com.stevenst.app.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
	private final UserRepository userRepo;
	private final PasswordEncoder passwordEncoder;
	private final JwtServiceImpl jwtService;
	private final AuthenticationManager authenticationManager;

	public AuthenticationResponse register(RegisterRequest request) {
		if (request.getEmail().isEmpty() || request.getPassword().isEmpty()) {
			throw new IllegalStateException("Credentials cannot be empty");
		}

		var user = User.builder()
				.firstname(request.getFirstName())
				.lastname(request.getLastName())
				.email(request.getEmail())
				.password(passwordEncoder.encode(request.getPassword()))
				.role(Role.USER)
				.build();

		userRepo.save(user);
		var jwtToken = jwtService.generateToken(user);

		return AuthenticationResponse.builder()
				.token(jwtToken)
				.build();
	}

	public AuthenticationResponse authenticate(AuthenticationRequest request) {
		try {
			authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
		} catch (AuthenticationException ex) {
			throw new BadCredentialsException("Authentication Failed");
		}

		var user = userRepo.findByEmail(request.getEmail())
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));
		var jwtToken = jwtService.generateToken(user);

		return AuthenticationResponse.builder()
				.token(jwtToken)
				.build();
	}
}
