package com.stevenst.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.stevenst.lib.model.Region;

public interface RegionRepository extends JpaRepository<Region, Long> {
	Long countByCountryName(String countryName);
}
