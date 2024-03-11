package com.stevenst.lib.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Region {
	private Long id;
	private String name;
	private Long country_id;
	private String country_code;
	private String country_name;
	private String state_code;
	private String type;
	private Double latitude;
	private Double longitude;
}
