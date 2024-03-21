package com.stevenst.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stevenst.lib.model.Country;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {
	
}
