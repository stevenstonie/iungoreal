package com.stevenst.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.stevenst.lib.model.SecondaryRegionsUsers;

import jakarta.transaction.Transactional;

@Repository
public interface SecondaryRegionsUsersRepository extends JpaRepository<SecondaryRegionsUsers, Long> {
	List<SecondaryRegionsUsers> findByUserId(Long userId);

	SecondaryRegionsUsers findByUserIdAndSecondaryRegionId(Long userId, Long secondaryRegionId);

	Long countByUserId(Long userId);
	
	@Transactional
	void removeAllByUserId(Long userId);


}
