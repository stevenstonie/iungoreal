package com.stevenst.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stevenst.app.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {  // TODO: swap 'Integer' with 'Long'
   Optional<User> findByEmail(String email);
}
