package com.stevenst.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.stevenst.lib.model.SecondaryRegionsUsers;

import jakarta.transaction.Transactional;

public interface SecondaryRegionsUsersRepository extends JpaRepository<SecondaryRegionsUsers, Long> {
	
	@Modifying
	@Transactional
	@Query("DELETE FROM SecondaryRegionsUsers sru WHERE sru.secondaryRegion.id IN :regionIds")
	void deleteSecondaryRegionsInList(List<Long> regionIds);
}
