package com.stevenst.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@EntityScan("com.stevenst.lib.model")
@SpringBootApplication
public class AppBackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(AppBackendApplication.class, args);
	}
}
