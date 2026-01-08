package com.book.management.api_gateway.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI/Swagger configuration for the API Gateway.
 * 
 * Provides API documentation for the gateway endpoints.
 * Access Swagger UI at: /swagger-ui.html
 * Access OpenAPI spec at: /v3/api-docs
 * 
 * @author Aditya Srivastava
 * @version 1.0
 * @since 2024-12-29
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Shelfie API Gateway")
                        .version("1.0.0")
                        .description("API Gateway for Shelfie Book Management System. " +
                                "This gateway routes requests to various microservices including " +
                                "Authentication, Book, Inventory, Order, Review, and User services.")
                        .contact(new Contact()
                                .name("Aditya Srivastava")
                                .email("aditya@example.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")));
    }
}
