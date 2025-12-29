package com.book.management.inventory.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Resilience4j configuration for fault tolerance patterns.
 * 
 * Implements:
 * - Circuit Breaker: Prevents cascading failures
 * - Retry: Automatic retry for transient failures
 * - Time Limiter: Timeout configuration
 * 
 * @author Digital Bookstore Team
 * @version 1.0
 * @since 2024-12-29
 */
@Configuration
@Slf4j
public class ResilienceConfig {

    /**
     * Circuit Breaker configuration bean.
     * 
     * Circuit Breaker States:
     * 1. CLOSED: Normal operation, calls go through
     * 2. OPEN: Circuit is broken, calls fail fast
     * 3. HALF_OPEN: Testing if service recovered
     * 
     * @return CircuitBreakerRegistry
     */
    @Bean
    public CircuitBreakerRegistry circuitBreakerRegistry() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                // Number of calls to evaluate for failure rate
                .slidingWindowSize(10)
                
                // Minimum number of calls before evaluating failure rate
                .minimumNumberOfCalls(5)
                
                // Percentage of failures to open circuit
                .failureRateThreshold(50.0f)
                
                // Time to wait before transitioning from OPEN to HALF_OPEN
                .waitDurationInOpenState(Duration.ofSeconds(10))
                
                // Number of calls in HALF_OPEN state
                .permittedNumberOfCallsInHalfOpenState(3)
                
                // Slow call duration threshold
                .slowCallDurationThreshold(Duration.ofSeconds(2))
                
                // Slow call rate threshold
                .slowCallRateThreshold(50.0f)
                
                // Record exceptions
                .recordExceptions(
                        Exception.class
                )
                
                // Don't record business exceptions
                .ignoreExceptions(
                        IllegalArgumentException.class
                )
                
                .build();

        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(config);
        
        // Add event listeners for logging
        registry.circuitBreaker("inventoryService").getEventPublisher()
                .onStateTransition(event -> 
                        log.warn("Circuit Breaker State Transition: {} -> {}", 
                                event.getStateTransition().getFromState(),
                                event.getStateTransition().getToState()))
                .onError(event -> 
                        log.error("Circuit Breaker Error: {}", 
                                event.getThrowable().getMessage()))
                .onSuccess(event -> 
                        log.debug("Circuit Breaker Success: Duration {} ms", 
                                event.getElapsedDuration().toMillis()));
        
        return registry;
    }

    /**
     * Retry configuration bean.
     * 
     * Implements exponential backoff retry strategy.
     * 
     * @return RetryRegistry
     */
    @Bean
    public RetryRegistry retryRegistry() {
        RetryConfig config = RetryConfig.custom()
                // Maximum retry attempts
                .maxAttempts(3)
                
                // Wait duration between retries
                .waitDuration(Duration.ofSeconds(1))
                
                // Exponential backoff multiplier
                .exponentialBackoffMultiplier(2)
                
                // Retry on specific exceptions
                .retryExceptions(
                        Exception.class
                )
                
                // Don't retry on specific exceptions
                .ignoreExceptions(
                        IllegalArgumentException.class
                )
                
                .build();

        RetryRegistry registry = RetryRegistry.of(config);
        
        // Add event listeners
        registry.retry("inventoryService").getEventPublisher()
                .onRetry(event -> 
                        log.warn("Retry attempt {} for operation", 
                                event.getNumberOfRetryAttempts()))
                .onSuccess(event -> 
                        log.info("Retry successful after {} attempts", 
                                event.getNumberOfRetryAttempts()))
                .onError(event -> 
                        log.error("All retry attempts failed: {}", 
                                event.getLastThrowable().getMessage()));
        
        return registry;
    }

    /**
     * Time Limiter configuration bean.
     * 
     * Prevents operations from running indefinitely.
     * 
     * @return TimeLimiterRegistry
     */
    @Bean
    public TimeLimiterRegistry timeLimiterRegistry() {
        TimeLimiterConfig config = TimeLimiterConfig.custom()
                // Maximum time allowed for operation
                .timeoutDuration(Duration.ofSeconds(5))
                
                // Cancel running future on timeout
                .cancelRunningFuture(true)
                
                .build();

        TimeLimiterRegistry registry = TimeLimiterRegistry.of(config);
        
        // Add event listeners
        registry.timeLimiter("inventoryService").getEventPublisher()
                .onTimeout(event -> 
                        log.error("Operation timed out after {} ms", 
                                event.getElapsedDuration().toMillis()))
                .onSuccess(event -> 
                        log.debug("Operation completed in {} ms", 
                                event.getElapsedDuration().toMillis()));
        
        return registry;
    }
}
