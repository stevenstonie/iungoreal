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

	public static List<Region> loadEntriesFromClasspath(String path) throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		Resource resource = new ClassPathResource(path);
		InputStream inputStream = resource.getInputStream();
		return objectMapper.readValue(inputStream, new TypeReference<List<Region>>() {
		});
	}
}
