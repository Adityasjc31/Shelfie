package com.book.management.user.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * AOP Aspect for comprehensive logging across all layers.
 * 
 * Provides cross-cutting logging concern for:
 * - Controller layer (REST endpoints)
 * - Service layer (business logic)
 * - Repository layer (data access)
 * 
 * Features:
 * - Method entry/exit logging with parameters
 * - Execution time tracking for performance monitoring
 * - Exception logging with full context
 * - Masked sensitive data (passwords)
 * 
 * @author Abdul Ahad
 * @version 1.0
 */
@Aspect
@Component
@Slf4j
public class LoggingAspect {

    // ==========================================
    // POINTCUT DEFINITIONS
    // ==========================================

    /**
     * Pointcut for all controller methods.
     */
    @Pointcut("execution(* com.book.management.user.controller..*.*(..))")
    public void controllerMethods() {
    }

    /**
     * Pointcut for all service methods.
     */
    @Pointcut("execution(* com.book.management.user.service..*.*(..))")
    public void serviceMethods() {
    }

    /**
     * Pointcut for all repository methods.
     */
    @Pointcut("execution(* com.book.management.user.repository..*.*(..))")
    public void repositoryMethods() {
    }

    /**
     * Combined pointcut for all application layers.
     */
    @Pointcut("controllerMethods() || serviceMethods() || repositoryMethods()")
    public void applicationMethods() {
    }

    // ==========================================
    // CONTROLLER LAYER LOGGING
    // ==========================================

    /**
     * Logs entry into controller methods with request details.
     */
    @Before("controllerMethods()")
    public void logControllerEntry(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        Object[] args = maskSensitiveData(joinPoint.getArgs());

        log.info("▶ REST REQUEST: {}.{} - Args: {}", className, methodName, Arrays.toString(args));
    }

    /**
     * Logs successful return from controller methods.
     */
    @AfterReturning(pointcut = "controllerMethods()", returning = "result")
    public void logControllerReturn(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        log.info("◀ REST RESPONSE: {}.{} - Success", className, methodName);
    }

    // ==========================================
    // SERVICE LAYER LOGGING WITH TIMING
    // ==========================================

    /**
     * Logs service method execution with timing information.
     * Uses @Around advice for precise timing measurement.
     */
    @Around("serviceMethods()")
    public Object logServiceMethodWithTiming(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        Object[] args = maskSensitiveData(joinPoint.getArgs());

        log.debug("→ SERVICE ENTER: {}.{} - Args: {}", className, methodName, Arrays.toString(args));

        long startTime = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;

            log.debug("← SERVICE EXIT: {}.{} - Time: {}ms", className, methodName, executionTime);

            // Log slow methods (> 500ms) at WARN level
            if (executionTime > 500) {
                log.warn("⚠ SLOW METHOD: {}.{} took {}ms", className, methodName, executionTime);
            }

            return result;
        } catch (Throwable ex) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("✗ SERVICE ERROR: {}.{} - Time: {}ms - Exception: {}",
                    className, methodName, executionTime, ex.getMessage());
            throw ex;
        }
    }

    // ==========================================
    // REPOSITORY LAYER LOGGING
    // ==========================================

    /**
     * Logs entry into repository methods.
     */
    @Before("repositoryMethods()")
    public void logRepositoryEntry(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        log.trace("⊕ REPOSITORY CALL: {}.{}", className, methodName);
    }

    /**
     * Logs successful return from repository methods.
     */
    @AfterReturning(pointcut = "repositoryMethods()", returning = "result")
    public void logRepositoryReturn(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        String resultInfo = result != null ? result.getClass().getSimpleName() : "null";
        log.trace("⊖ REPOSITORY RETURN: {}.{} - Result: {}", className, methodName, resultInfo);
    }

    // ==========================================
    // EXCEPTION LOGGING
    // ==========================================

    /**
     * Logs all exceptions thrown from application methods.
     */
    @AfterThrowing(pointcut = "applicationMethods()", throwing = "exception")
    public void logException(JoinPoint joinPoint, Throwable exception) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        log.error("✗ EXCEPTION in {}.{}: {} - {}",
                className, methodName,
                exception.getClass().getSimpleName(),
                exception.getMessage());
    }

    // ==========================================
    // UTILITY METHODS
    // ==========================================

    /**
     * Masks sensitive data in method arguments.
     * Replaces password fields with [PROTECTED].
     * 
     * @param args the original method arguments
     * @return array with masked sensitive data
     */
    private Object[] maskSensitiveData(Object[] args) {
        if (args == null || args.length == 0) {
            return args;
        }

        Object[] maskedArgs = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg == null) {
                maskedArgs[i] = null;
            } else {
                String argString = arg.toString();
                // Mask password fields
                if (argString.toLowerCase().contains("password")) {
                    maskedArgs[i] = "[CONTAINS SENSITIVE DATA]";
                } else {
                    maskedArgs[i] = arg;
                }
            }
        }
        return maskedArgs;
    }
}
