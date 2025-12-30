package com.book.management.inventory.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Configuration class for Inventory Service microservice components.
 * 
 * Configures:
 * - REST Template for inter-service communication
 * - OpenAPI/Swagger documentation
 * - Transaction management
 * - CORS settings
 * 
 * @author Aditya Srivastava
 * @version 1.0
 * @since 2024-12-29
 */
@Configuration
@EnableTransactionManagement
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
     * Provides detailed API documentation for the Inventory Service.
     * 
     * @return OpenAPI configuration
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(List.of(
                        new Server().url("http://localhost:9080").description("API Gateway"),
                        new Server().url("http://localhost:9081").description("Direct Access (Dev Only)")
                ))
                .info(new Info()
                        .title("Inventory Service API")
                        .version("1.0")
                        .description("""
                                REST API for Inventory Management in Digital Bookstore.
                                
                                Features:
                                - Stock level management
                                - Low stock alerts
                                - Inventory adjustments
                                - Bulk operations
                                - Real-time availability checks
                                
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
