package com.book.management.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Configuration for async operations in Order Service.
 * Provides a custom thread pool for parallel Feign calls to improve performance.
 * 
 * Per LLD Section 4.3 - Order Management Module:
 * - Enables users to place, track, and manage orders
 * - Optimizes order placement by parallelizing service calls
 *
 * @author Rehan Ashraf
 * @version 1.0
 * @since 2026-01-06
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * Creates a thread pool executor for async Feign operations.
     * Used for parallel calls to Book Service and Inventory Service.
     * 
     * @return configured executor for async operations
     */
    @Bean(name = "feignExecutor")
    public Executor feignExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("feign-async-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();
        return executor;
    }
}
