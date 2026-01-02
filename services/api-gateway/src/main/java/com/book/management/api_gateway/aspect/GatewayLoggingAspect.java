package com.book.management.api_gateway.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;


import java.time.Duration;
import java.time.Instant;


/**
 * AOP Aspect for Gateway logging.
 * 
 * Provides comprehensive logging for:
 * - Incoming requests (method, path, headers)
 * - Routing decisions
 * - Filter executions
 * - Response status and timing
 * - Exceptions and errors
 * 
 * Uses reactive logging patterns suitable for Spring Cloud Gateway.
 * 
 * @author Aditya Srivastava
 * @version 1.0
 * @since 2024-12-29
 */
@Aspect
@Component
@Slf4j
public class GatewayLoggingAspect {

    /**
     * Pointcut for all filter methods.
     */
    @Pointcut("execution(* com.book.management.book.api_gateway.filter..*(..))")
    public void filterLayer() {}

    /**
     * Pointcut for all controller methods (fallback controllers).
     */
    @Pointcut("execution(* com.book.management.book.api_gateway.controller..*(..))")
    public void controllerLayer() {}

    /**
     * Pointcut for route configuration methods.
     */
    @Pointcut("execution(* com.book.management.book.api_gateway.config..*(..))")
    public void configLayer() {}

    /**
     * Around advice for filter methods.
     * Logs filter execution with timing and details.
     * 
     * @param joinPoint the join point
     * @return the method result
     * @throws Throwable if method execution fails
     */
    @Around("filterLayer()")
    public Object logAroundFilter(ProceedingJoinPoint joinPoint) throws Throwable {
        Instant startTime = Instant.now();
        
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        
        log.info("╔════════════════════════════════════════════════════════════");
        log.info("║ GATEWAY FILTER: {}.{}", 
                className.substring(className.lastIndexOf('.') + 1), methodName);
        
        // Log ServerWebExchange details if present
        for (Object arg : args) {
            if (arg instanceof ServerWebExchange) {
                ServerWebExchange exchange = (ServerWebExchange) arg;
                log.info("║ Method: {}", exchange.getRequest().getMethod());
                log.info("║ Path: {}", exchange.getRequest().getPath());
                log.info("║ Remote Address: {}", 
                        exchange.getRequest().getRemoteAddress());
                break;
            }
        }
        
        try {
            Object result = joinPoint.proceed();
            
            Duration executionTime = Duration.between(startTime, Instant.now());
            log.info("║ Filter Execution Time: {} ms", executionTime.toMillis());
            log.info("║ Status: SUCCESS");
            log.info("╚════════════════════════════════════════════════════════════");
            
            return result;
        } catch (Exception e) {
            Duration executionTime = Duration.between(startTime, Instant.now());
            log.error("║ Filter Execution Time: {} ms", executionTime.toMillis());
            log.error("║ Status: FAILED");
            log.error("║ Exception: {} - {}", e.getClass().getSimpleName(), e.getMessage());
            log.info("╚════════════════════════════════════════════════════════════");
            throw e;
        }
    }

    /**
     * Around advice for fallback controller methods.
     * Logs fallback execution when circuit breaker opens.
     * 
     * @param joinPoint the join point
     * @return the method result
     * @throws Throwable if method execution fails
     */
    @Around("controllerLayer()")
    public Object logAroundController(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        
        log.warn("▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓");
        log.warn("▓ FALLBACK ACTIVATED: {}", methodName);
        log.warn("▓ Circuit breaker opened - Routing to fallback");
        log.warn("▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓");
        
        return joinPoint.proceed();
    }

    /**
     * Before advice for configuration methods.
     * Logs configuration initialization.
     * 
     * @param joinPoint the join point
     */
    @Before("configLayer()")
    public void logBeforeConfig(JoinPoint joinPoint) {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        
        log.info("⚙ GATEWAY CONFIG: Initializing {}.{}", 
                className.substring(className.lastIndexOf('.') + 1), methodName);
    }

    /**
     * After returning advice for configuration methods.
     * Logs successful configuration.
     * 
     * @param joinPoint the join point
     * @param result the returned result
     */
    @AfterReturning(pointcut = "configLayer()", returning = "result")
    public void logAfterConfig(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        
        log.info("✓ GATEWAY CONFIG: {} completed successfully", methodName);
    }

    /**
     * After throwing advice for all layers.
     * Logs exceptions with full context.
     * 
     * @param joinPoint the join point
     * @param exception the thrown exception
     */
    @AfterThrowing(pointcut = "filterLayer() || controllerLayer() || configLayer()", 
                   throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        
        log.error("╔════════════════════════════════════════════════════════════");
        log.error("║ GATEWAY EXCEPTION");
        log.error("║ Location: {}.{}", className, methodName);
        log.error("║ Exception Type: {}", exception.getClass().getName());
        log.error("║ Message: {}", exception.getMessage());
        log.error("║ Stack Trace:");
        
        StackTraceElement[] stackTrace = exception.getStackTrace();
        for (int i = 0; i < Math.min(5, stackTrace.length); i++) {
            log.error("║   at {}", stackTrace[i]);
        }
        
        log.error("╚════════════════════════════════════════════════════════════");
    }

    /**
     * Pointcut for methods annotated with @LogExecutionTime.
     */
    @Pointcut("@annotation(com.book.management.book.api_gateway.annotation.LogExecutionTime)")
    public void logExecutionTime() {}

    /**
     * Around advice for methods with @LogExecutionTime annotation.
     * Provides detailed performance metrics.
     * 
     * @param joinPoint the join point
     * @return the method result
     * @throws Throwable if method execution fails
     */
    @Around("logExecutionTime()")
    public Object logMethodExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        Instant startTime = Instant.now();
        
        Object result = joinPoint.proceed();
        
        Duration executionTime = Duration.between(startTime, Instant.now());
        String methodName = joinPoint.getSignature().getName();
        
        log.info("⏱ PERFORMANCE: {} executed in {} ms", methodName, executionTime.toMillis());
        
        if (executionTime.toMillis() > 1000) {
            log.warn("⚠ SLOW OPERATION: {} took {} ms (> 1 second)", 
                    methodName, executionTime.toMillis());
        }
        
        return result;
    }
}
