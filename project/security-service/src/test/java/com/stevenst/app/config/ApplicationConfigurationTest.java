package com.stevenst.app.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.stevenst.lib.model.Role;
import com.stevenst.lib.model.User;
import com.stevenst.app.repository.UserRepository;
import com.stevenst.app.service.impl.UserDetailsServiceImpl;

class ApplicationConfigurationTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationConfiguration authenticationConfiguration;

    @Mock
    private UserDetailsServiceImpl userDetailsServiceImpl;

    @InjectMocks
    private ApplicationConfiguration applicationConfiguration;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUserDetailsService_WhenUserFound_ReturnsUserDetails() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(
                Optional.of(User
                        .builder()
                        .email("test@example.com")
                        .password("password")
                        .role(Role.USER)
                        .build()));

        UserDetails userDetails = applicationConfiguration.userDetailsService().loadUserByUsername("test@example.com");

        assertNotNull(userDetails);
        assertEquals("test@example.com", userDetails.getUsername());
    }

    @Test
    void testUserDetailsService_UserNotFound() {
        String email = "test@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            applicationConfiguration.userDetailsService().loadUserByUsername(email);
        });
    }

    @Test
    void testAuthenticationProvider_ReturnsDaoAuthenticationProvider() {
        AuthenticationProvider authenticationProvider = applicationConfiguration.authenticationProvider();

        assertNotNull(authenticationProvider);
        assertTrue(authenticationProvider instanceof DaoAuthenticationProvider);
    }

    @Test
    void testPasswordEncoder_ReturnsBCryptPasswordEncoder() {
        BCryptPasswordEncoder passwordEncoder = applicationConfiguration.passwordEncoder();

        assertNotNull(passwordEncoder);
        assertTrue(passwordEncoder instanceof BCryptPasswordEncoder);
    }
}