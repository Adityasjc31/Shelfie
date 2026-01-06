package com.book.management.user.client.fallback;

import com.book.management.user.client.OrderServiceClient;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

/**
 * Fallback Factory implementation for OrderServiceClient.
 * 
 * Provides graceful degradation when the Order Service is unavailable.
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
public class OrderServiceClientFallbackFactory implements FallbackFactory<OrderServiceClient> {

    @Override
    public OrderServiceClient create(Throwable cause) {
        return new OrderServiceClient() {

            /**
             * Fallback method for deleting orders by user ID.
             * Logs a warning with exception details but doesn't fail the operation.
             * 
             * @param userId the user ID
             * @return ResponseEntity with no content
             */
            @Override
            public ResponseEntity<Void> deleteOrdersByUserId(Long userId) {
                logFallback("deleteOrdersByUserId", userId, cause);
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
            log.warn("FALLBACK [{}]: Order Service call failed. Param: {}. Status: {}. Message: {}",
                    methodName, param, feignException.status(), errorMessage);
        } else {
            log.warn("FALLBACK [{}]: Order Service unavailable. Param: {}. Cause: {} - {}",
                    methodName, param, cause.getClass().getSimpleName(), errorMessage);
        }
    }
}
