package com.book.management.inventory.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation for logging method execution time.
 * 
 * When applied to a method, the LoggingAspect will automatically
 * log the execution time and warn if it exceeds performance thresholds.
 * 
 * Usage:
 * <pre>
 * {@code
 * @LogExecutionTime
 * public void someMethod() {
 *     // method implementation
 * }
 * }
 * </pre>
 * 
 * @author Aditya Srivastava
 * @version 2.0
 * @since 2024-12-29
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LogExecutionTime {
}
