package com.stevenst.app.service;

import java.util.Date;
import java.util.function.Function;

import org.springframework.stereotype.Service;

import com.stevenst.app.model.User;

import io.jsonwebtoken.Claims;

@Service
public interface JwtService {
	String extractEmail(String token);

	<T> T extractClaim(String token, Function<Claims, T> claimsResolver);

	String generateToken(String email);
	
	String generateToken(User user);

	boolean isTokenValid(String token, User user);

	boolean isTokenValid(String token, String email);

	boolean isTokenExpired(String token);

	Date extractExpiration(String token);

	String extractToken(String authHeader);
}
