package com.stevenst.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.stevenst.app.model.Marker;

public interface MarkerRepository extends JpaRepository<Marker, Long> {
	
}
