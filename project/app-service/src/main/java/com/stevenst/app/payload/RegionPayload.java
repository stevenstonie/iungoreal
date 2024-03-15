package com.stevenst.app.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegionPayload {
	private Long id;
	private Long countryId;
	private String name;
	private String code;
	private Double latitude;
	private Double longitude;
}
