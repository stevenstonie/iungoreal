package com.stevenst.app.util;

import java.util.Date;
import java.util.HashMap;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.stevenst.app.model.Role;
import com.stevenst.app.model.User;
import com.stevenst.app.repository.UserRepository;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TestUtil {
    private final UserRepository userRepository;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public void insertUserIntoDB(String email, String password, String username, Role role)
            throws Exception {
        User user = new User(0L, email, passwordEncoder.encode(password), username, role);

        userRepository.save(user);
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