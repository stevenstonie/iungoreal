package com.stevenst.app.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.stevenst.lib.model.Country;
import com.stevenst.lib.model.Region;

public class CountryFromJson extends Country {
	private List<Region> regions = new ArrayList<>();

	public List<Region> getRegions() {
		return regions;
	}

	public void addRegion(Region region) {
		this.regions.add(region);
	}

	public Country convertToCountry() {
		return Country.builder()
				.id(this.getId())
				.name(this.getName())
				.iso3(this.getIso3())
				.iso2(this.getIso2())
				.numericCode(this.getNumericCode())
				.phoneCode(this.getPhoneCode())
				.capital(this.getCapital())
				.currency(this.getCurrency())
				.currencyCode(this.getCurrencyCode())
				.nationality(this.getNationality())
				.latitude(this.getLatitude())
				.longitude(this.getLongitude())
				.build();
	}

	@Override
	public boolean equals(Object thatObj) {
		if (this == thatObj) {
			return true;
		}
		if (thatObj == null || getClass() != thatObj.getClass()) {
			return false;
		}
		CountryFromJson thisObj = (CountryFromJson) thatObj;
		return Objects.equals(regions, thisObj.regions);
	}

	@Override
	public int hashCode() {
		return Objects.hash(regions);
	}
}	
