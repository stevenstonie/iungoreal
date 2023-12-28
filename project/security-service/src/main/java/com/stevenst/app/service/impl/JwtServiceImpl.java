package com.stevenst.app.service.impl;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.stevenst.app.model.User;
import com.stevenst.app.service.JwtService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtServiceImpl implements JwtService {
	@Value("${jwt.secret-key}")
	private String secretKey;
	private static final long EXPIRATION_TIME = 24L * 60L * 60L * 1000L; // 24 hours

	@Override
	public String extractEmail(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	@Override
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	@Override
	public String generateToken(String email) {
		return generateToken(new HashMap<>(), email);
	}

	@Override
	public String generateToken(User user) {
		return generateToken(new HashMap<>(), user);
	}

	@Override
	public boolean isTokenValid(String token, User user) {
		final String extractedEmail = extractEmail(token);
		return (extractedEmail.equals(user.getEmail()) && !isTokenExpired(token));
	}

	@Override
	public boolean isTokenValid(String token, String email) {
		final String extractedEmail = extractEmail(token);
		return (extractedEmail.equals(email) && !isTokenExpired(token));
	}

	@Override
	public boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	@Override
	public Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}

	@Override
	public String extractToken(String authHeader) {
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			throw new IllegalArgumentException("Invalid Authorization header");
		}
		return authHeader.substring(7);
	}

	private String generateToken(Map<String, Object> extraClaims, User user) {
		return Jwts
				.builder()
				.claims(extraClaims)
				.subject(user.getEmail())
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
				.signWith(getSignInKey())
				.compact();
	}
	// TODO: check if the key is enough without the signature algorithm

	private String generateToken(Map<String, Object> extraClaims, String email) {
		return Jwts
				.builder()
				.claims(extraClaims)
				.subject(email)
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
				.signWith(getSignInKey())
				.compact();
	}

	private Claims extractAllClaims(String token) {
		Key key = getSignInKey();
		SecretKey secretKey2 = new SecretKeySpec(key.getEncoded(), key.getAlgorithm());

		return Jwts.parser()
				.verifyWith(secretKey2)
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

	private Key getSignInKey() {
		byte[] keyBites = Decoders.BASE64.decode(secretKey);
		return Keys.hmacShaKeyFor(keyBites);
	}
}

//TODO: test this class (ex: check if the token returns the right email)
