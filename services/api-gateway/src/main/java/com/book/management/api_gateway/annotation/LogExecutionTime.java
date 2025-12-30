package com.book.management.api_gateway.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation for logging method execution time.
 * 
 * When applied to a method, the GatewayLoggingAspect will automatically
 * log the execution time and warn if it exceeds performance thresholds.
 * 
 * Usage:
 * <pre>
 * {@code
 * @LogExecutionTime
 * public Mono<String> someMethod() {
 *     // method implementation
 * }
 * }
 * </pre>
 * 
 * @author Digital Bookstore Team
 * @version 1.0
 * @since 2024-12-29
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogExecutionTime {
    
    /**
     * Custom threshold in milliseconds for warning about slow execution.
     * Default is 1000ms (1 second).
     */
    long threshold() default 1000;
}
