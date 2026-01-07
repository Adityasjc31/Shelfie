package com.book.management.user.client;

import com.book.management.user.dto.ReviewResponseDTO;
import com.book.management.user.client.fallback.ReviewServiceClientFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Feign Client for inter-service communication with Review Service.
 * 
 * Enables the User Service to:
 * - Fetch reviews by user
 * - Delete reviews when a user is deleted (cascade)
 * 
 * Uses FallbackFactory for resilience and detailed error logging
 * when Review Service is unavailable.
 * 
 * Microservices Pattern: Service-to-Service Communication via FeignClient
 * - Declarative REST client
 * - Integrated with Eureka for service discovery
 * - Circuit breaker support via FallbackFactory
 * 
 * @author Aditya Srivastava
 * @version 1.0
 * @since 2026-01-06
 */
@FeignClient(name = "review-service", fallbackFactory = ReviewServiceClientFallbackFactory.class)
public interface ReviewServiceClient {

    /**
     * Retrieves all reviews by a specific user.
     * 
     * @param userId the user ID
     * @return List of ReviewResponseDTO containing user's reviews
     */
    @GetMapping("/api/v1/reviews/user/{userId}")
    List<ReviewResponseDTO> getReviewsByUserId(@PathVariable("userId") Long userId);

    /**
     * Deletes a review by review ID and user ID.
     * 
     * @param reviewId the review ID
     * @param userId   the user ID
     * @return ResponseEntity with no content
     */
    @DeleteMapping("/api/v1/reviews/{reviewId}")
    ResponseEntity<Void> deleteReview(@PathVariable("reviewId") Long reviewId,
            @RequestParam("userId") Long userId);

    /**
     * Deletes a review by admin.
     * 
     * @param reviewId the review ID
     * @return ResponseEntity with no content
     */
    @DeleteMapping("/api/v1/reviews/{reviewId}/admin")
    ResponseEntity<Void> deleteReviewByAdmin(@PathVariable("reviewId") Long reviewId);

    /**
     * Deletes all reviews by a specific user.
     * Used for cascading delete when a user is deleted.
     * 
     * @param userId the user ID whose reviews should be deleted
     * @return ResponseEntity with no content
     */
    @DeleteMapping("/api/v1/reviews/user/{userId}")
    ResponseEntity<Void> deleteReviewsByUserId(@PathVariable("userId") Long userId);
}
