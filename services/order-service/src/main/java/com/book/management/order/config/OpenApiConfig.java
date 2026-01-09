package com.book.management.order.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger configuration for Order Service.
 * Provides detailed API documentation for the Order Management endpoints.
 * 
 * @author Rehan Ashraf
 * @version 1.0
 * @since 2024-12-29
 */
@Configuration
public class OpenApiConfig {

    /**
     * OpenAPI configuration for Swagger documentation.
     * Provides detailed API documentation for the Order Service.
     * 
     * @return OpenAPI configuration
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .servers(List.of(
                        new Server().url("http://localhost:9080").description("API Gateway"),
                        new Server().url("http://localhost:9085").description("Direct Access (Dev Only)")
                ))
                .info(new Info()
                        .title("Order Service API")
                        .version("1.0")
                        .description("""
                                REST API for Order Management in Digital Bookstore.
                                
                                Features:
                                - Create and manage customer orders
                                - Order status tracking and updates
                                - Order history retrieval
                                - Order cancellation
                                - Integration with Book and Inventory services
                                - User-specific order queries
                                
                                Order Statuses:
                                - PENDING: Order created, awaiting processing
                                - CONFIRMED: Order confirmed and being prepared
                                - SHIPPED: Order dispatched for delivery
                                - DELIVERED: Order successfully delivered
                                - CANCELLED: Order cancelled by user or system
                                
                                Authentication: All requests must be authenticated via API Gateway.
                                """)
                        .contact(new Contact()
                                .name("Rehan Ashraf")
                                .email("support@digitalbookstore.com")
                                .url("https://digitalbookstore.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")));
    }
}
