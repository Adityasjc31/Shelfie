package com.book.management.book.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Configuration class for Book Service microservice components.
 * 
 * Configures:
 * - REST Template for inter-service communication
 * - OpenAPI/Swagger documentation
 * - Transaction management
 * 
 * @author Aditya Srivastava
 * @version 1.0
 * @since 2024-12-29
 */
@Configuration
public class MicroserviceConfig {

    @Value("${spring.application.name}")
    private String applicationName;

    /**
     * Bean for RestTemplate to enable inter-service communication.
     * Can be used to call other microservices.
     * 
     * @return RestTemplate instance
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * OpenAPI configuration for Swagger documentation.
     * Provides detailed API documentation for the Book Service.
     * 
     * @return OpenAPI configuration
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(List.of(
                        new Server().url("http://localhost:9080").description("API Gateway"),
                        new Server().url("http://localhost:9082").description("Direct Access (Dev Only)")
                ))
                .info(new Info()
                        .title("Book Service API")
                        .version("1.0")
                        .description("""
                                REST API for Book Management in Digital Bookstore.
                                
                                Features:
                                - Book CRUD operations
                                - Book search and filtering
                                - Category management
                                - Book inventory integration
                                - Review integration
                                
                                Authentication: All requests must be authenticated via API Gateway.
                                """)
                        .contact(new Contact()
                                .name("Aditya Srivastava")
                                .email("support@digitalbookstore.com")
                                .url("https://digitalbookstore.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}
