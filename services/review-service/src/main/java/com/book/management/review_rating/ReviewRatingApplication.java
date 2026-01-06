package com.book.management.review_rating;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Main Application class for Review & Rating Service.

 * This microservice handles all review-related operations including:
 * - Creating and managing book reviews
 * - Rating aggregation and statistics
 * - Review moderation (approve/reject)
 * - Helpful review marking

 * Integrates with:
 * - Config Server for centralized configuration
 * - Eureka Server for service discovery
 * - Book Service for book validation (via FeignClient)
 * - API Gateway for routing and authentication
 * 
 * @author Aditya Srivastava
 * @version 2.0
 * @since 2026-01-01
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableJpaAuditing
@EnableAspectJAutoProxy
public class ReviewRatingApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReviewRatingApplication.class, args);
	}

}
