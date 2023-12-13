package com.stevenst.app.service;

import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;

@Service
public interface JwtService {
	String extractUsername(String token);

	<T> T extractClaim(String token, Function<Claims, T> claimsResolver);

	String generateToken(UserDetails userDetails);

	String generateToken(Map<String, Object> extraClaims, UserDetails userDetails);

	boolean isTokenValid(String token, UserDetails userDetails);

	boolean isTokenValid(String token, String email);

	boolean isTokenExpired(String token);

	Date extractExpiration(String token);

	String extractToken(String authHeader);
}
