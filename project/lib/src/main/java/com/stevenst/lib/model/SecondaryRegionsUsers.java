package com.stevenst.lib.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "\"secondary_regions_users\"")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecondaryRegionsUsers {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "\"user_id\"", nullable = false)
	private User user;

	@ManyToOne(optional = false)
	@JoinColumn(name = "\"secondary_region_id\"", nullable = false)
	private Region secondaryRegion;
}
