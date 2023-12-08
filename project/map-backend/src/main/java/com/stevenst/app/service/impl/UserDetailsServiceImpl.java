package com.stevenst.app.service.impl;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.stevenst.app.model.User;
import com.stevenst.app.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
   private final UserRepository userRepository;

   public UserDetailsServiceImpl(UserRepository userRepository) {
       this.userRepository = userRepository;
   }

   @Override
   public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
       Optional<User> user = userRepository.findByEmail(username);
       if (user.isEmpty()) {
           throw new UsernameNotFoundException("User not found");
       }
       return new org.springframework.security.core.userdetails.User(user.get().getUsername(), user.get().getPassword(), new ArrayList<>());
   }
}
