package com.stevenst.lib.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "\"region\"")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Region implements Serializable {
	@Id
	@Column(nullable = false, unique = true)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "\"country_id\"", nullable = false)
	private Country country;

	@Column(nullable = false)
	private String name;

	private String code;

	@Column(nullable = false)
	private Double latitude;
	
	@Column(nullable = false)
	private Double longitude;
	
	private String type;
}
