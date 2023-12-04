package com.stevenst.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.stevenst.app.service.impl.JwtServiceImpl;

@SpringBootApplication
public class App {
	public static void main(String[] args) {
		var bean = SpringApplication.run(App.class, args);
		JwtServiceImpl jwtService = bean.getBean(JwtServiceImpl.class);
		System.out.println(jwtService.getSecretKey());
	}
}
