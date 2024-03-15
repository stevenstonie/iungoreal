package com.stevenst.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stevenst.lib.model.Country;
import com.stevenst.lib.model.Region;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {
	Long countByCountry(Country country);

	List<Region> findAllByCountry(Country country);
}
