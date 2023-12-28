package com.stevenst.app.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.stevenst.app.exception.IgorAuthenticationException;
import com.stevenst.app.model.Role;
import com.stevenst.app.model.User;
import com.stevenst.app.repository.UserRepository;

class ApplicationConfigurationTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationConfiguration authenticationConfiguration;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private ApplicationConfiguration applicationConfiguration;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void userDetailsService() {
        when(userRepository.findByEmail("test@email.com")).thenReturn(
                Optional.of(User
                        .builder()
                        .email("test@email.com")
                        .password("testpassword")
                        .username("testusername")
                        .role(Role.USER)
                        .build()));

        User user = (User) applicationConfiguration.userDetailsService().loadUserByUsername("test@email.com");

        assertNotNull(user);
        assertEquals("test@email.com", user.getEmail());
    }

    @Test
    void userDetailsService_userNotFound() {
        String email = "test@email.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        var exception = assertThrows(IgorAuthenticationException.class, () -> {
            applicationConfiguration.userDetailsService().loadUserByUsername(email);
        });
        assertEquals("Email not found", exception.getMessage());
    }

    @Test
    void authenticationProvider_ReturnsDaoAuthenticationProvider() {
        AuthenticationProvider authenticationProvider = applicationConfiguration.authenticationProvider();

        assertNotNull(authenticationProvider);
        assertTrue(authenticationProvider instanceof DaoAuthenticationProvider);
    }

    @Test
    void passwordEncoder_ReturnsBCryptPasswordEncoder() {
        BCryptPasswordEncoder passwordEncoder = applicationConfiguration.passwordEncoder();

        assertNotNull(passwordEncoder);
        assertTrue(passwordEncoder instanceof BCryptPasswordEncoder);
    }
}