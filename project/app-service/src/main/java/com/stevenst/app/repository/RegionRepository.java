package com.stevenst.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stevenst.lib.model.Region;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {
	Long countByCountryName(String countryName);
}
