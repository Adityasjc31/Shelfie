package com.book.management.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Entry point for the Order Microservice.
 * * @EnableFeignClients: Enables inter-service communication via interfaces.
 *   @EnableDiscoveryClient:  Registers this service with the Eureka Registry.
 */
@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
public class OrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }
}

