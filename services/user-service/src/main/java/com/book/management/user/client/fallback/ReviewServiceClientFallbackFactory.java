package com.book.management.user.client.fallback;

import com.book.management.user.client.ReviewServiceClient;
import com.book.management.user.dto.ReviewResponseDTO;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Fallback Factory implementation for ReviewServiceClient.
 * 
 * Provides graceful degradation when the Review Service is unavailable.
 * Uses FallbackFactory pattern to capture and log the actual exception
 * that caused the fallback, enabling better debugging and monitoring.
 * 
 * Resilience Pattern: Circuit Breaker with FallbackFactory
 * - Provides exception context for better error handling
 * - Prevents service failures from propagating
 * - Logs detailed warnings for monitoring and alerting
 * - Allows core operations to continue with degraded functionality
 * 
 * @author Aditya Srivastava
 * @version 1.0
 * @since 2026-01-06
 */
@Component
@Slf4j
public class ReviewServiceClientFallbackFactory implements FallbackFactory<ReviewServiceClient> {

    @Override
    public ReviewServiceClient create(Throwable cause) {
        return new ReviewServiceClient() {

            /**
             * Fallback method when review-service is unavailable.
             * Returns empty list and logs a warning with exception details.
             * 
             * @param userId the user ID
             * @return empty list (fallback response)
             */
            @Override
            public List<ReviewResponseDTO> getReviewsByUserId(Long userId) {
                logFallback("getReviewsByUserId", userId, cause);
                return Collections.emptyList();
            }

            /**
             * Fallback method for deleting a review.
             * Logs a warning with exception details but doesn't fail the operation.
             * 
             * @param reviewId the review ID
             * @param userId   the user ID
             * @return ResponseEntity with no content
             */
            @Override
            public ResponseEntity<Void> deleteReview(Long reviewId, Long userId) {
                logFallback("deleteReview", "reviewId=" + reviewId + ", userId=" + userId, cause);
                return ResponseEntity.noContent().build();
            }

            /**
             * Fallback method for admin review deletion.
             * Logs a warning with exception details but doesn't fail the operation.
             * 
             * @param reviewId the review ID
             * @return ResponseEntity with no content
             */
            @Override
            public ResponseEntity<Void> deleteReviewByAdmin(Long reviewId) {
                logFallback("deleteReviewByAdmin", reviewId, cause);
                return ResponseEntity.noContent().build();
            }
        };
    }

    /**
     * Logs fallback invocation with exception details.
     * 
     * @param methodName the method that triggered fallback
     * @param param      the parameter value
     * @param cause      the exception that caused the fallback
     */
    private void logFallback(String methodName, Object param, Throwable cause) {
        String errorMessage = cause.getMessage();
        
        if (cause instanceof FeignException feignException) {
            log.warn("FALLBACK [{}]: Review Service call failed. Param: {}. Status: {}. Message: {}",
                    methodName, param, feignException.status(), errorMessage);
        } else {
            log.warn("FALLBACK [{}]: Review Service unavailable. Param: {}. Cause: {} - {}",
                    methodName, param, cause.getClass().getSimpleName(), errorMessage);
        }
    }
}
