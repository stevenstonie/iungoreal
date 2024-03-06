package com.stevenst.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    private static final String MAPPING = "/api/**";
    private static final String ORIGINS = "http://localhost:4200";

    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping(MAPPING)
                .allowedOrigins(ORIGINS)
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowCredentials(true)
                .allowedHeaders("Authorization", "Content-Type");
    }
}
