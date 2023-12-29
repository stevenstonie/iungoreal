package com.stevenst.app.service.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.stevenst.app.auth.AuthRequest;
import com.stevenst.app.auth.RegisterRequest;
import com.stevenst.app.exception.IgorAuthenticationException;
import com.stevenst.lib.model.Role;
import com.stevenst.lib.model.User;
import com.stevenst.app.repository.UserRepository;

class AuthenticationServiceImplTest {
	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private JwtServiceImpl jwtService;

	@Mock
	private AuthenticationManager authenticationManager;

	@InjectMocks
	private AuthenticationServiceImpl authenticationService;

	User user = new User(0L, "normal@email.com", "password", "first", "last", Role.USER);

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void register_validCredentials() {
		RegisterRequest request = new RegisterRequest(user.getEmail(), user.getPassword(), user.getFirstname(),
				user.getLastname());

		when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
		when(passwordEncoder.encode(anyString())).thenReturn(user.getPassword());
		when(jwtService.generateToken(anyString())).thenReturn("token");

		assertDoesNotThrow(() -> authenticationService.register(request));
	}

	@Test
	void register_withEmptyCredentials() {
		RegisterRequest requestWithEmptyEmail = new RegisterRequest("", user.getPassword(), user.getFirstname(),
				user.getLastname());
		RegisterRequest requestWithEmptyPassword = new RegisterRequest(user.getEmail(), "", user.getFirstname(),
				user.getLastname());

		assertThrows(IgorAuthenticationException.class, () -> authenticationService.register(requestWithEmptyEmail));
		assertThrows(IgorAuthenticationException.class, () -> authenticationService.register(requestWithEmptyPassword));
	}

	@Test
	void register_theUserAlreadyExists() {
		RegisterRequest request = new RegisterRequest(user.getEmail(), user.getPassword(), user.getFirstname(),
				user.getLastname());

		when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));

		assertThrows(IgorAuthenticationException.class, () -> authenticationService.register(request));
	}

	@Test
	void register_emptyRequest() {
		RegisterRequest request = new RegisterRequest();

		assertThrows(IgorAuthenticationException.class, () -> authenticationService.register(request));
	}

	@Test
	void login_validCredentials() {
		AuthRequest request = new AuthRequest(user.getEmail(), user.getPassword());

		when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
		when(jwtService.generateToken(user)).thenReturn("token");

		assertDoesNotThrow(() -> authenticationService.login(request));
	}

	@Test
	void login_emptyCredentials() {
		AuthRequest requestWithEmptyEmail = new AuthRequest("", user.getPassword());
		AuthRequest requestWithEmptyPassword = new AuthRequest(user.getEmail(), "");

		assertThrows(IgorAuthenticationException.class, () -> authenticationService.login(requestWithEmptyEmail));
		assertThrows(IgorAuthenticationException.class, () -> authenticationService.login(requestWithEmptyPassword));
	}

	@Test
	void login_userDoesntExist() {
		AuthRequest request = new AuthRequest(user.getEmail(), user.getPassword());

		when(authenticationManager.authenticate(any())).thenThrow(new UsernameNotFoundException("User not found"));

		assertThrows(IgorAuthenticationException.class, () -> authenticationService.login(request));
	}

	@Test
	void login_emptyRequest() {
		AuthRequest authRequest = new AuthRequest();

		assertThrows(IgorAuthenticationException.class, () -> authenticationService.login(authRequest));
	}

}
