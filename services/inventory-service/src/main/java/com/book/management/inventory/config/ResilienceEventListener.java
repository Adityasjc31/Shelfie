package com.book.management.inventory.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Event listeners for Resilience4j components.
 * 
 * Circuit breaker and retry configurations are managed via
 * application.properties
 * (centralized in config-service). This class only handles event logging.
 * 
 * @author Aditya Srivastava
 * @version 2.0
 * @since 2026-01-03
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class ResilienceEventListener {

    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final RetryRegistry retryRegistry;

    @PostConstruct
    public void registerEventListeners() {
        // Register circuit breaker event listeners
        circuitBreakerRegistry.getAllCircuitBreakers().forEach(this::registerCircuitBreakerEvents);

        // Register retry event listeners
        retryRegistry.getAllRetries().forEach(this::registerRetryEvents);

        // Listen for new circuit breakers/retries added dynamically
        circuitBreakerRegistry.getEventPublisher()
                .onEntryAdded(event -> registerCircuitBreakerEvents(event.getAddedEntry()));

        retryRegistry.getEventPublisher()
                .onEntryAdded(event -> registerRetryEvents(event.getAddedEntry()));

        log.info("Resilience4j event listeners registered successfully");
    }

    private void registerCircuitBreakerEvents(CircuitBreaker circuitBreaker) {
        circuitBreaker.getEventPublisher()
                .onStateTransition(event -> log.warn("Circuit Breaker [{}] State Transition: {} -> {}",
                        circuitBreaker.getName(),
                        event.getStateTransition().getFromState(),
                        event.getStateTransition().getToState()))
                .onError(event -> log.error("Circuit Breaker [{}] Error: {}",
                        circuitBreaker.getName(),
                        event.getThrowable().getMessage()))
                .onSuccess(event -> log.debug("Circuit Breaker [{}] Success: Duration {} ms",
                        circuitBreaker.getName(),
                        event.getElapsedDuration().toMillis()));
    }

    private void registerRetryEvents(Retry retry) {
        retry.getEventPublisher()
                .onRetry(event -> log.warn("Retry [{}] attempt {} for operation",
                        retry.getName(),
                        event.getNumberOfRetryAttempts()))
                .onSuccess(event -> log.info("Retry [{}] successful after {} attempts",
                        retry.getName(),
                        event.getNumberOfRetryAttempts()))
                .onError(event -> log.error("Retry [{}] All attempts failed: {}",
                        retry.getName(),
                        event.getLastThrowable().getMessage()));
    }
}
