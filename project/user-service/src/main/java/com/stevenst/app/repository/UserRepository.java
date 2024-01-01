package com.stevenst.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.stevenst.lib.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	public Optional<User> findByUsername(String username);

	public Optional<User> findByEmail(String email);

	@Query("SELECT u.id FROM User u WHERE u.username = :username")
	public Optional<Long> findIdByUsername(String username);
}
