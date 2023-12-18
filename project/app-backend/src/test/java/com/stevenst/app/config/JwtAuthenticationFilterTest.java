package com.stevenst.app.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.stevenst.app.service.impl.JwtServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    private final String token = "random_jwt_token";
    private final String userEmail = "random_name@email.com";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testDoFilterInternal_InvokesFilterChainDoFilter() throws ServletException, IOException {
        mockTokenRequest(true);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_WithValidToken_ShouldAuthenticateUser() throws ServletException, IOException {
        mockTokenRequest(true);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(jwtService, times(1)).extractUsername(anyString());
        verify(userDetailsService, times(1)).loadUserByUsername(anyString());
        verify(jwtService, times(1)).isTokenValid(anyString(), any(UserDetails.class));
    }

    @Test
    void testDoFilterInternal_WithInvalidToken_UserNotFound() throws ServletException, IOException {
        mockTokenRequest(false);
        when(userDetailsService.loadUserByUsername(anyString())).thenThrow(UsernameNotFoundException.class);
        when(jwtService.isTokenValid(anyString(), any(UserDetails.class))).thenReturn(false);

        assertThrows(UsernameNotFoundException.class,
                () -> jwtAuthenticationFilter.doFilterInternal(request, response, filterChain));

        verify(jwtService, times(1)).extractUsername(anyString());
        verify(userDetailsService, times(1)).loadUserByUsername(anyString());
        verify(jwtService, times(0)).isTokenValid(anyString(), any(UserDetails.class));
    }

    @Test
    void testDoFilterInternal_WithMissingAuthorizationHeader_ShouldNotAuthenticateUser()
            throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(jwtService, times(0)).extractUsername(anyString());
        verify(userDetailsService, times(0)).loadUserByUsername(anyString());
        verify(jwtService, times(0)).isTokenValid(anyString(), any(UserDetails.class));
    }

    @Test
    void testDoFilterInternal_WithValidTokenAndExistingAuthentication_ShouldNotOverwriteAuthentication()
            throws ServletException, IOException {
        mockTokenRequest(true);
        Authentication existingAuth = mock(Authentication.class);
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(existingAuth);
        SecurityContextHolder.setContext(securityContext);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertSame(existingAuth, SecurityContextHolder.getContext().getAuthentication(),
                "Existing authentication should not be overwritten");
    }

    @Test
    void testDoFilterInternal_WithTokenNotStartingWithBearer_ShouldNotAuthenticateUser()
            throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Random " + this.token);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(jwtService, times(0)).extractUsername(anyString());
        verify(userDetailsService, times(0)).loadUserByUsername(anyString());
        verify(jwtService, times(0)).isTokenValid(anyString(), any(UserDetails.class));
    }

    @Test
    void testDoFilterInternal_WithEmptyToken_ShouldNotAuthenticateUser() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer ");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(jwtService, times(0)).extractUsername(anyString());
        verify(userDetailsService, times(0)).loadUserByUsername(anyString());
        verify(jwtService, times(0)).isTokenValid(anyString(), any(UserDetails.class));
    }

    // -------------------------------------------------

    private void mockTokenRequest(boolean isValid) {
        when(request.getHeader("Authorization")).thenReturn("Bearer " + this.token);
        when(jwtService.extractUsername(anyString())).thenReturn(this.userEmail);

        if (isValid) {
            UserDetails userDetails = mock(UserDetails.class);
            when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
            when(jwtService.isTokenValid(anyString(), any(UserDetails.class))).thenReturn(true);
        }
    }
}