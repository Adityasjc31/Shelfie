package com.book.management.inventory.utils;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Aspect for cross-cutting concerns - primarily logging.
 * 
 * This aspect provides comprehensive logging for:
 * - Controller layer (HTTP requests/responses)
 * - Service layer (business logic execution)
 * - Repository layer (database operations)
 * - Exception handling
 * 
 * Uses Spring AOP to intercept method calls and log details
 * without polluting business logic code.
 * 
 * @author Digital Bookstore Team
 * @version 1.0
 * @since 2024-12-29
 */
@Aspect
@Component
@Slf4j
public class LoggingAspect {

    /**
     * Pointcut for all methods in controller package.
     */
    @Pointcut("execution(* com.book.management.inventory.controller..*(..))")
    public void controllerLayer() {}

    /**
     * Pointcut for all methods in service package.
     */
    @Pointcut("execution(* com.book.management.inventory.service..*(..))")
    public void serviceLayer() {}

    /**
     * Pointcut for all methods in repository package.
     */
    @Pointcut("execution(* com.book.management.inventory.repository..*(..))")
    public void repositoryLayer() {}

    /**
     * Around advice for controller methods.
     * Logs HTTP request entry, execution time, and response.
     * 
     * @param joinPoint the join point
     * @return the method result
     * @throws Throwable if method execution fails
     */
    @Around("controllerLayer()")
    public Object logAroundController(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        
        log.info("═══════════════════════════════════════════════════════");
        log.info("→ CONTROLLER REQUEST: {}.{}", className, methodName);
        log.info("→ Arguments: {}", Arrays.toString(args));
        
        Object result = null;
        try {
            result = joinPoint.proceed();
            
            long executionTime = System.currentTimeMillis() - startTime;
            log.info("← CONTROLLER RESPONSE: {}.{}", className, methodName);
            log.info("← Execution Time: {} ms", executionTime);
            log.info("← Response: {}", result != null ? result.getClass().getSimpleName() : "void");
            log.info("═══════════════════════════════════════════════════════");
            
            return result;
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("✗ CONTROLLER ERROR: {}.{}", className, methodName);
            log.error("✗ Execution Time: {} ms", executionTime);
            log.error("✗ Exception: {} - {}", e.getClass().getSimpleName(), e.getMessage());
            log.info("═══════════════════════════════════════════════════════");
            throw e;
        }
    }

    /**
     * Around advice for service methods.
     * Logs service method execution with timing.
     * 
     * @param joinPoint the join point
     * @return the method result
     * @throws Throwable if method execution fails
     */
    @Around("serviceLayer()")
    public Object logAroundService(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        
        log.debug("▶ SERVICE CALL: {}.{}", className, methodName);
        log.debug("▶ Parameters: {}", Arrays.toString(args));
        
        try {
            Object result = joinPoint.proceed();
            
            long executionTime = System.currentTimeMillis() - startTime;
            log.debug("◀ SERVICE RETURN: {}.{} ({} ms)", className, methodName, executionTime);
            
            return result;
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("✗ SERVICE ERROR: {}.{} ({} ms)", className, methodName, executionTime);
            log.error("✗ Exception Type: {}", e.getClass().getSimpleName());
            log.error("✗ Error Message: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Before advice for repository methods.
     * Logs database operation before execution.
     * 
     * @param joinPoint the join point
     */
    @Before("repositoryLayer()")
    public void logBeforeRepository(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        
        log.trace("⚡ DB OPERATION: {} with args: {}", methodName, Arrays.toString(args));
    }

    /**
     * After returning advice for repository methods.
     * Logs successful database operation completion.
     * 
     * @param joinPoint the join point
     * @param result the returned result
     */
    @AfterReturning(pointcut = "repositoryLayer()", returning = "result")
    public void logAfterRepository(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        
        if (result != null) {
            log.trace("✓ DB SUCCESS: {} returned {}", methodName, result.getClass().getSimpleName());
        } else {
            log.trace("✓ DB SUCCESS: {} completed", methodName);
        }
    }

    /**
     * After throwing advice for all layers.
     * Logs exceptions with full context.
     * 
     * @param joinPoint the join point
     * @param exception the thrown exception
     */
    @AfterThrowing(pointcut = "controllerLayer() || serviceLayer() || repositoryLayer()", 
                   throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        
        log.error("╔════════════════════════════════════════════════════════════");
        log.error("║ EXCEPTION CAUGHT");
        log.error("║ Location: {}.{}", className, methodName);
        log.error("║ Exception Type: {}", exception.getClass().getName());
        log.error("║ Message: {}", exception.getMessage());
        log.error("║ Stack Trace:");
        
        // Log first 5 stack trace elements for context
        StackTraceElement[] stackTrace = exception.getStackTrace();
        for (int i = 0; i < Math.min(5, stackTrace.length); i++) {
            log.error("║   at {}", stackTrace[i]);
        }
        
        log.error("╚════════════════════════════════════════════════════════════");
    }

    /**
     * Pointcut for methods annotated with custom @LogExecutionTime.
     * Can be used for specific performance monitoring.
     */
    @Pointcut("@annotation(com.book.management.inventory.annotation.LogExecutionTime)")
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
        long startTime = System.currentTimeMillis();
        
        Object result = joinPoint.proceed();
        
        long executionTime = System.currentTimeMillis() - startTime;
        String methodName = joinPoint.getSignature().getName();
        
        log.info("⏱ PERFORMANCE: {} executed in {} ms", methodName, executionTime);
        
        if (executionTime > 1000) {
            log.warn("⚠ SLOW OPERATION: {} took {} ms (> 1 second)", methodName, executionTime);
        }
        
        return result;
    }
}
