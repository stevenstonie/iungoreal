package com.stevenst.app.service.impl;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.stevenst.app.exception.IgorAuthenticationException;
import com.stevenst.lib.model.User;
import com.stevenst.app.repository.AuthRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final AuthRepository authRepository;

    public UserDetailsServiceImpl(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = authRepository.findByEmail(email);

        if (user.isEmpty()) {
            throw new IgorAuthenticationException("Email not found");
        }
        
        return new org.springframework.security.core.userdetails.User(user.get().getEmail(),
                user.get().getPassword(),
                new ArrayList<>());
    }

    public UserDetails convertUserToUserDetails(User user) {
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
                new ArrayList<>());
    }
}
