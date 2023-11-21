package com.stevenst.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stevenst.app.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {
   User findByUsername(String username);
}

