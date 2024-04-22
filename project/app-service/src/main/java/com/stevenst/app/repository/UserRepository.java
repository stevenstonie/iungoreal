package com.stevenst.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.stevenst.app.payload.UserPublicPayload;
import com.stevenst.lib.model.User;

import jakarta.transaction.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUsername(String username);

	@Transactional
	@Modifying
	@Query("UPDATE User u " +
			"SET u.primaryRegionId = CASE WHEN u.primaryRegionId IN :regionIds THEN null ELSE u.primaryRegionId END, " +
			"u.countryId = CASE WHEN u.countryId = :countryId THEN null ELSE u.countryId END " +
			"WHERE (u.countryId = :countryId OR u.primaryRegionId IN :regionIds)")
	void updateCountryAndPrimaryRegionToNullByCountryIdAndRegionIds(Long countryId, List<Long> regionIds);
	// for each user it will remove the country if it matches and the region if its inside the list

	@Query("SELECT u FROM User u WHERE u.username LIKE %:input%")
	List<User> findUsersByUsernameContaining(String input);
}
