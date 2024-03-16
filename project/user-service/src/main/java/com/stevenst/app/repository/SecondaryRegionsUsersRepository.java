package com.stevenst.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stevenst.lib.model.SecondaryRegionsUsers;

@Repository
public interface SecondaryRegionsUsersRepository extends JpaRepository<SecondaryRegionsUsers, Long> {
	
}
