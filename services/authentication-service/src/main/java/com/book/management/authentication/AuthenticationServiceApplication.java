package com.book.management.authentication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Authentication Service Application
 * 
 * Centralized authentication and authorization service for the
 * Digital Bookstore microservices ecosystem.
 * 
 * Features:
 * - User registration and login
 * - JWT token generation and validation
 * - Session management
 * - Token refresh mechanism
 * - Role-based access control
 * - Password encryption (BCrypt)
 * - Integration with User Service
 * 
 * Security Flow:
 * 1. User registers/logs in → Receives JWT token
 * 2. User makes API call with token in Authorization header
 * 3. API Gateway validates token with this service
 * 4. If valid, request proceeds to downstream service
 * 
 * @author Aditya Srivastava
 * @version 1.0
 * @since 2024-12-30
 */
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
public class AuthenticationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthenticationServiceApplication.class, args);
    }
}
