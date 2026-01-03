package com.book.management.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * User Service Application
 * 
 * Microservice for user management in the Digital Bookstore ecosystem.
 * 
 * Features:
 * - User registration and profile management
 * - User authentication support (via authentication-service)
 * - Role-based user categorization (CUSTOMER/ADMIN)
 * - Spring Data JPA for persistence
 * - AOP-based logging
 * - Eureka service discovery
 * - Spring Cloud Config integration
 * 
 * @author Abdul Ahad
 * @version 2.0 - Microservice Architecture
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableAspectJAutoProxy
public class UserApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserApplication.class, args);
	}

}
