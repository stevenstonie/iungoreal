package com.stevenst.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@EntityScan("com.stevenst.lib.model")
@SpringBootApplication
public class SecurityBackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(SecurityBackendApplication.class, args);
	}
}
