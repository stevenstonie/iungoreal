package com.stevenst.app.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stevenst.lib.model.Region;

public class JsonUtil {
	private JsonUtil() {
	}

	// TODO: search if its better to use throws here and catch where its called or catch here
	public static CountryFromJson loadCountryAndRegionsFromJsonClasspath(String countryName, String jsonFileName)
			throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		Resource resource = new ClassPathResource(jsonFileName);
		InputStream inputStream = resource.getInputStream();

		List<CountryFromJson> countries = objectMapper.readValue(inputStream,
				new TypeReference<List<CountryFromJson>>() {
				});

		CountryFromJson countryFromJson = countries.stream()
				.filter(country -> countryName.equals(country.getName()))
				.findFirst()
				.orElse(null);

		if (countryFromJson != null) {
			for (Region region : countryFromJson.getRegions()) {
				region.setCountryName(countryFromJson.getName());
			}
		}
		return countryFromJson;

		// try (JsonParser parser = objectMapper.getFactory().createParser(inputStream)) {
		// 	while (parser.nextToken() != null) {
		// 		if ("name".equals(parser.getCurrentName()) && countryName.equals(parser.getValueAsString())) {
		// 			return objectMapper.readValue(parser, CountryFromJson.class);
		// 		}
		// 	}
		// }
		// // TODO: cannot do it this way because of some parsing issues (try to fix it though)
		// return null;
	}
}
