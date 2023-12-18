package com.stevenst.app.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.stevenst.app.model.Role;
import com.stevenst.app.model.User;
import com.stevenst.app.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TestUtil {
    private final UserRepository userRepository;

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private static final String EMAIL = "testuser123";
    private static final String PASSWORD = "testpassword123";

    public void insertUserIntoDB() throws Exception {
        User user = new User(0L, EMAIL, passwordEncoder.encode(PASSWORD), "test", "user", Role.USER);

        userRepository.save(user);
    }
}