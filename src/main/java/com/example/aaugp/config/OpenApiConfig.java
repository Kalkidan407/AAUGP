package com.example.aaugp.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;

import io.swagger.v3.oas.annotations.info.Info;


@Configuration

@OpenAPIDefinition(
   

         info = @Info(
        title = "AAUGP API",
        version = "1.0",
        description = "Addis Ababa University Graduation Project Platform API" )
    
        // security = @SecurityRequirement(name = "bearerAuth")
)

// @SecurityScheme(
//         name = "bearerAuth",
//         description = "JWT Authentication",
//         scheme = "bearer",
//         type = SecuritySchemeType.HTTP,
//         bearerFormat = "JWT",
//         in = SecuritySchemeIn.HEADER
        
// )
public class OpenApiConfig {
    
}
