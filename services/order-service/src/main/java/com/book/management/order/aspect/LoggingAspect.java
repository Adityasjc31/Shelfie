package com.book.management.order.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * AOP Aspect for centralized logging across the Order service.
 * 
 * Microservices Architecture Benefits:
 * - Centralized logging logic
 * - Reduced code duplication
 * - Easy to modify logging behavior
 * - Consistent logging format across all layers
 * - Performance monitoring capabilities
 * 
 * Logging Strategy:
 * - Controller layer: Request/Response logging
 * - Service layer: Business logic execution logging
 * - Repository layer: Database operation logging
 * - Exception logging: Automatic error tracking
 * 
 * @author Aditya Srivastava
 * @version 1.0
 * @since 2026-01-03
 */
@Aspect
@Component
@Slf4j
public class LoggingAspect {

    /**
     * Pointcut for all controller methods.
     */
    @Pointcut("within(com.book.management.order.controller..*)")
    public void controllerLayer() {
    }

    /**
     * Pointcut for all service methods.
     */
    @Pointcut("within(com.book.management.order.service..*)")
    public void serviceLayer() {
    }

    /**
     * Pointcut for all repository methods.
     */
    @Pointcut("within(com.book.management.order.repository..*)")
    public void repositoryLayer() {
    }

    /**
     * Logs method entry with parameters for all public methods.
     */
    @Before("controllerLayer() || serviceLayer()")
    public void logMethodEntry(JoinPoint joinPoint) {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        log.info("→ Entering: {}.{}() with arguments: {}",
                className, methodName, Arrays.toString(args));
    }

    /**
     * Logs method exit with return value.
     */
    @AfterReturning(pointcut = "controllerLayer() || serviceLayer()", returning = "result")
    public void logMethodExit(JoinPoint joinPoint, Object result) {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        log.info("← Exiting: {}.{}() with result: {}",
                className, methodName, result != null ? result.getClass().getSimpleName() : "void");
    }

    /**
     * Logs method execution time and handles exceptions.
     */
    @Around("controllerLayer() || serviceLayer()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;

            log.info("⏱ {}.{}() executed in {} ms",
                    className, methodName, executionTime);

            return result;
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("⚠ {}.{}() failed after {} ms with exception: {}",
                    className, methodName, executionTime, e.getMessage());
            throw e;
        }
    }

    /**
     * Logs all exceptions thrown from service layer.
     */
    @AfterThrowing(pointcut = "serviceLayer()", throwing = "exception")
    public void logException(JoinPoint joinPoint, Throwable exception) {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();

        log.error("✗ Exception in {}.{}(): {} - {}",
                className, methodName, exception.getClass().getSimpleName(), exception.getMessage());
    }

    /**
     * Logs database operations.
     */
    @Around("repositoryLayer()")
    public Object logDatabaseOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();

        log.debug("🗄 Database operation: {} with args: {}",
                methodName, Arrays.toString(joinPoint.getArgs()));

        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - startTime;

        log.debug("🗄 Database operation {} completed in {} ms", methodName, executionTime);

        return result;
    }
}
