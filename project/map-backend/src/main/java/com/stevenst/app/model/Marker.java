package com.stevenst.app.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "marker")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Marker {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(nullable = false)
	private String title;
	
	private String description;

	@Column(nullable = false)
	private double latitude;

	@Column(nullable = false)
	private double longitude;

	private String startDate;

	@Column(nullable = false)
	private String endDate;
	// TODO: these markers should be automatically removed from the db when the end date is reached
}
