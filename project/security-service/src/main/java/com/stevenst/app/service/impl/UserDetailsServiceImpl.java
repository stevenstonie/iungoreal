package com.stevenst.app.service.impl;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.stevenst.app.exception.IgorAuthenticationException;
import com.stevenst.lib.model.User;
import com.stevenst.app.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(email);

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
