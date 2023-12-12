package com.stevenst.app.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import com.stevenst.app.service.impl.JwtServiceImpl;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

class JwtAuthenticationFilterTest {
    @Mock
    private JwtServiceImpl jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);
    }

    @Test
    void doFilterInternal_WithValidToken_ShouldAuthenticateUser() throws ServletException, IOException {
        String token = "working_jwt_token";
        String userEmail = "test@example.com";

        mockTokenRequest(token, userEmail, true);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(jwtService, times(1)).extractUsername(anyString());
        verify(userDetailsService, times(1)).loadUserByUsername(anyString());
        verify(jwtService, times(1)).isTokenValid(anyString(), any(UserDetails.class));
    }

    @Test
    void doFilterInternal_WithInvalidToken_ShouldNotAuthenticateUser() throws ServletException, IOException {
        String token = "random_jwt_token";
        String userEmail = "name@email.com";

        mockTokenRequest(token, userEmail, false);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(jwtService, times(1)).extractUsername(anyString());
        verify(userDetailsService, times(0)).loadUserByUsername(anyString());
        verify(jwtService, times(0)).isTokenValid(anyString(), any(UserDetails.class));
    }

    private void mockTokenRequest(String token, String userEmail, boolean isValid) {
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractUsername(token)).thenReturn(userEmail);

        if (isValid) {
            UserDetails userDetails = mock(UserDetails.class);
            when(userDetailsService.loadUserByUsername(userEmail)).thenReturn(userDetails);
            when(jwtService.isTokenValid(token, userDetails)).thenReturn(true);
        }
    }
}
