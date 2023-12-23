package com.stevenst.app.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
@OpenAPIDefinition
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer")
public class SpringdocConfig {
    @Bean
    OpenAPI baseOpenAPI() {
        return new OpenAPI().info(new Info().title("iungoreal endpoints").version("1.0.0")
                .description("below are the endpoints for the iungoreal app. use a valid bearer token to access the endpoints"));
    }
}