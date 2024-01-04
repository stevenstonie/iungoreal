package com.stevenst.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stevenst.lib.model.User;

@Repository
public interface TestRepository extends JpaRepository<User, Long> {
	
}
