package com.stevenst.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stevenst.lib.model.Country;

public interface CountryRepository extends JpaRepository<Country, Long> {
	
}
