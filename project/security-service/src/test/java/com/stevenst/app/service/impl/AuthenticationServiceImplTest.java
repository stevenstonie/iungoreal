package com.stevenst.app.service.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.stevenst.app.auth.AuthRequest;
import com.stevenst.app.auth.RegisterRequest;
import com.stevenst.app.exception.IgorAuthenticationException;
import com.stevenst.app.model.Role;
import com.stevenst.app.model.User;
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

	User user = new User(0L, "test@email.com", "testpassword", "testusername", Role.USER);

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void register() {
		RegisterRequest request = new RegisterRequest(user.getEmail(), user.getPassword(), user.getUsername());

		when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
		when(passwordEncoder.encode(anyString())).thenReturn(user.getPassword());
		when(jwtService.generateToken(anyString())).thenReturn("token");

		assertDoesNotThrow(() -> authenticationService.register(request));

		verify(userRepository, times(1)).findByEmail(anyString());
		verify(userRepository, times(1)).findByUsername(anyString());
		verify(passwordEncoder, times(1)).encode(anyString());
		verify(userRepository, times(1)).save(any(User.class));
		verify(jwtService, times(1)).generateToken(any(User.class));
	}

	@Test
	void login() {
		AuthRequest request = new AuthRequest(user.getEmail(), user.getPassword());

		when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
		when(jwtService.generateToken(user)).thenReturn("token");

		assertDoesNotThrow(() -> authenticationService.login(request));

		verify(authenticationManager, times(1)).authenticate(any());
		verify(userRepository, times(1)).findByEmail(anyString());
		verify(jwtService, times(1)).generateToken(user);
	}

	@Test
	void register_withEmptyCredentials() {
		RegisterRequest requestWithEmptyEmail = new RegisterRequest("", user.getPassword(), user.getUsername());
		RegisterRequest requestWithEmptyPassword = new RegisterRequest(user.getEmail(), "", user.getUsername());
		RegisterRequest requestWithEmptyUsername = new RegisterRequest(user.getEmail(), user.getPassword(), "");

		var emptyEmailException = assertThrows(IgorAuthenticationException.class,
				() -> authenticationService.register(requestWithEmptyEmail));
		var emptyPasswordException = assertThrows(IgorAuthenticationException.class,
				() -> authenticationService.register(requestWithEmptyPassword));
		var emptyUsernameException = assertThrows(IgorAuthenticationException.class,
				() -> authenticationService.register(requestWithEmptyUsername));

		assertEquals("Credentials cannot be empty", emptyEmailException.getMessage());
		assertEquals("Credentials cannot be empty", emptyPasswordException.getMessage());
		assertEquals("Credentials cannot be empty", emptyUsernameException.getMessage());
	}

	@Test
	void register_withNullCredentials() {
		RegisterRequest requestWithNullEmail = new RegisterRequest(null, user.getPassword(), user.getUsername());
		RegisterRequest requestWithNullPassword = new RegisterRequest(user.getEmail(), null, user.getUsername());
		RegisterRequest requestWithNullUsername = new RegisterRequest(user.getEmail(), user.getPassword(), null);

		var nullEmailException = assertThrows(IgorAuthenticationException.class,
				() -> authenticationService.register(requestWithNullEmail));
		var nullPasswordException = assertThrows(IgorAuthenticationException.class,
				() -> authenticationService.register(requestWithNullPassword));
		var nullUsernameException = assertThrows(IgorAuthenticationException.class,
				() -> authenticationService.register(requestWithNullUsername));

		assertEquals("Credentials cannot be empty", nullEmailException.getMessage());
		assertEquals("Credentials cannot be empty", nullPasswordException.getMessage());
		assertEquals("Credentials cannot be empty", nullUsernameException.getMessage());
	}

	@Test
	void register_emailAlreadyExists() {
		RegisterRequest request = new RegisterRequest(user.getEmail(), user.getPassword(), user.getUsername());

		when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));

		var alreadyExistsException = assertThrows(IgorAuthenticationException.class,
				() -> authenticationService.register(request));

		assertEquals("Email already taken", alreadyExistsException.getMessage());
	}

	@Test
	void register_usernameAlreadyExists() {
		RegisterRequest request = new RegisterRequest(user.getEmail(), user.getPassword(), user.getUsername());

		when(userRepository.findByUsername(request.getUsername())).thenReturn(Optional.of(user));

		var alreadyExistsException = assertThrows(IgorAuthenticationException.class,
				() -> authenticationService.register(request));

		assertEquals("Username already taken", alreadyExistsException.getMessage());
	}

	@Test
	void register_emptyRequest() {
		RegisterRequest request = new RegisterRequest();

		var emptyRequestException = assertThrows(IgorAuthenticationException.class,
				() -> authenticationService.register(request));

		assertEquals("Credentials cannot be empty", emptyRequestException.getMessage());
	}

	@Test
	void login_emptyCredentials() {
		AuthRequest requestWithEmptyEmail = new AuthRequest("", user.getPassword());
		AuthRequest requestWithEmptyPassword = new AuthRequest(user.getEmail(), "");

		var emptyEmailException = assertThrows(IgorAuthenticationException.class,
				() -> authenticationService.login(requestWithEmptyEmail));
		var emptyPasswordException = assertThrows(IgorAuthenticationException.class,
				() -> authenticationService.login(requestWithEmptyPassword));

		assertEquals("Credentials cannot be empty", emptyEmailException.getMessage());
		assertEquals("Credentials cannot be empty", emptyPasswordException.getMessage());
	}

	@Test
	void login_userDoesntExist() {
		AuthRequest request = new AuthRequest(user.getEmail(), user.getPassword());

		when(authenticationManager.authenticate(any())).thenThrow(new IgorAuthenticationException("User not found"));

		var userNotFoundException = assertThrows(IgorAuthenticationException.class,
				() -> authenticationService.login(request));

		assertEquals("User not found", userNotFoundException.getMessage());
	}

	@Test
	void login_emptyRequest() {
		AuthRequest authRequest = new AuthRequest();

		var emptyRequestException = assertThrows(IgorAuthenticationException.class,
				() -> authenticationService.login(authRequest));
		
		assertEquals("Credentials cannot be empty", emptyRequestException.getMessage());
	}
}
