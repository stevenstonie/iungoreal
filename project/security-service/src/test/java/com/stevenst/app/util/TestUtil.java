package com.stevenst.app.util;

import java.util.Date;
import java.util.HashMap;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.stevenst.lib.model.enums.Role;
import com.stevenst.lib.model.User;
import com.stevenst.app.repository.AuthRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TestUtil {
    private final AuthRepository authRepository;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public void insertUserIntoDB(String email, String password, String username, Role role)
            throws Exception {
        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .username(username)
                .build();

        authRepository.save(user);
    }

    public String generateTokenWithBadSignature(String email) {
        return Jwts
                .builder()
                .claims(new HashMap<>())
                .subject(email)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 60 * 60 * 1000))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(
                        "1234abcd1234abcd1234abcd1234abcd1234abcd1234abcd1234abcd1234abcd")))
                .compact();
    }
}