package com.book.management.review_rating.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for LoggingAspect.
 * Tests AOP logging functionality for controller and service layers.
 *
 * @author Bookstore Development Team
 * @version 1.0
 * @since 2024-12-15
 */
@ExtendWith(MockitoExtension.class)
class LoggingAspectTest {

    private LoggingAspect loggingAspect;

    @Mock
    private JoinPoint joinPoint;

    @Mock
    private ProceedingJoinPoint proceedingJoinPoint;

    @Mock
    private Signature signature;

    @BeforeEach
    void setUp() {
        loggingAspect = new LoggingAspect();
    }

    @Nested
    @DisplayName("Method Entry Logging Tests")
    class MethodEntryLoggingTests {

        @Test
        @DisplayName("logMethodEntry should log method entry with arguments")
        void logMethodEntry_LogsMethodWithArguments() {
            when(joinPoint.getSignature()).thenReturn(signature);
            when(signature.getDeclaringTypeName()).thenReturn("com.test.TestController");
            when(signature.getName()).thenReturn("testMethod");
            when(joinPoint.getArgs()).thenReturn(new Object[] { "arg1", 123 });

            // Should not throw exception
            assertDoesNotThrow(() -> loggingAspect.logMethodEntry(joinPoint));
        }

        @Test
        @DisplayName("logMethodEntry should handle empty arguments")
        void logMethodEntry_HandlesEmptyArguments() {
            when(joinPoint.getSignature()).thenReturn(signature);
            when(signature.getDeclaringTypeName()).thenReturn("com.test.TestController");
            when(signature.getName()).thenReturn("noArgsMethod");
            when(joinPoint.getArgs()).thenReturn(new Object[] {});

            assertDoesNotThrow(() -> loggingAspect.logMethodEntry(joinPoint));
        }
    }

    @Nested
    @DisplayName("Method Exit Logging Tests")
    class MethodExitLoggingTests {

        @Test
        @DisplayName("logMethodExit should log method exit with result")
        void logMethodExit_LogsMethodWithResult() {
            when(joinPoint.getSignature()).thenReturn(signature);
            when(signature.getDeclaringTypeName()).thenReturn("com.test.TestService");
            when(signature.getName()).thenReturn("getReview");

            assertDoesNotThrow(() -> loggingAspect.logMethodExit(joinPoint, "result"));
        }

        @Test
        @DisplayName("logMethodExit should handle null result")
        void logMethodExit_HandlesNullResult() {
            when(joinPoint.getSignature()).thenReturn(signature);
            when(signature.getDeclaringTypeName()).thenReturn("com.test.TestService");
            when(signature.getName()).thenReturn("deleteReview");

            assertDoesNotThrow(() -> loggingAspect.logMethodExit(joinPoint, null));
        }
    }

    @Nested
    @DisplayName("Execution Time Logging Tests")
    class ExecutionTimeLoggingTests {

        @Test
        @DisplayName("logExecutionTime should log execution time and return result")
        void logExecutionTime_LogsTimeAndReturnsResult() throws Throwable {
            when(proceedingJoinPoint.getSignature()).thenReturn(signature);
            when(signature.getDeclaringTypeName()).thenReturn("com.test.TestService");
            when(signature.getName()).thenReturn("processReview");
            when(proceedingJoinPoint.proceed()).thenReturn("expectedResult");

            Object result = loggingAspect.logExecutionTime(proceedingJoinPoint);

            assertEquals("expectedResult", result);
            verify(proceedingJoinPoint).proceed();
        }

        @Test
        @DisplayName("logExecutionTime should log error and rethrow exception")
        void logExecutionTime_LogsErrorAndRethrowsException() throws Throwable {
            when(proceedingJoinPoint.getSignature()).thenReturn(signature);
            when(signature.getDeclaringTypeName()).thenReturn("com.test.TestService");
            when(signature.getName()).thenReturn("failingMethod");
            RuntimeException testException = new RuntimeException("Test error");
            when(proceedingJoinPoint.proceed()).thenThrow(testException);

            RuntimeException thrown = assertThrows(RuntimeException.class,
                    () -> loggingAspect.logExecutionTime(proceedingJoinPoint));

            assertEquals("Test error", thrown.getMessage());
        }
    }

    @Nested
    @DisplayName("Exception Logging Tests")
    class ExceptionLoggingTests {

        @Test
        @DisplayName("logException should log exception details")
        void logException_LogsExceptionDetails() {
            when(joinPoint.getSignature()).thenReturn(signature);
            when(signature.getDeclaringTypeName()).thenReturn("com.test.TestService");
            when(signature.getName()).thenReturn("methodWithException");
            Exception testException = new RuntimeException("Test exception message");

            assertDoesNotThrow(() -> loggingAspect.logException(joinPoint, testException));
        }

        @Test
        @DisplayName("logException should handle different exception types")
        void logException_HandlesDifferentExceptionTypes() {
            when(joinPoint.getSignature()).thenReturn(signature);
            when(signature.getDeclaringTypeName()).thenReturn("com.test.TestService");
            when(signature.getName()).thenReturn("methodWithException");

            assertDoesNotThrow(() -> loggingAspect.logException(joinPoint,
                    new IllegalArgumentException("Bad argument")));
            assertDoesNotThrow(() -> loggingAspect.logException(joinPoint,
                    new NullPointerException("Null value")));
        }
    }

    @Nested
    @DisplayName("Database Operation Logging Tests")
    class DatabaseOperationLoggingTests {

        @Test
        @DisplayName("logDatabaseOperation should log operation and return result")
        void logDatabaseOperation_LogsAndReturnsResult() throws Throwable {
            when(proceedingJoinPoint.getSignature()).thenReturn(signature);
            when(signature.getName()).thenReturn("findById");
            when(proceedingJoinPoint.getArgs()).thenReturn(new Object[] { 1L });
            when(proceedingJoinPoint.proceed()).thenReturn("dbResult");

            Object result = loggingAspect.logDatabaseOperation(proceedingJoinPoint);

            assertEquals("dbResult", result);
            verify(proceedingJoinPoint).proceed();
        }

        @Test
        @DisplayName("logDatabaseOperation should propagate exceptions")
        void logDatabaseOperation_PropagatesExceptions() throws Throwable {
            when(proceedingJoinPoint.getSignature()).thenReturn(signature);
            when(signature.getName()).thenReturn("saveReview");
            when(proceedingJoinPoint.getArgs()).thenReturn(new Object[] {});
            when(proceedingJoinPoint.proceed()).thenThrow(new RuntimeException("DB error"));

            assertThrows(RuntimeException.class,
                    () -> loggingAspect.logDatabaseOperation(proceedingJoinPoint));
        }
    }

    @Nested
    @DisplayName("Pointcut Definition Tests")
    class PointcutDefinitionTests {

        @Test
        @DisplayName("controllerLayer pointcut should be defined")
        void controllerLayer_IsDefined() {
            // Just verify the method exists and can be called without exception
            assertDoesNotThrow(() -> loggingAspect.controllerLayer());
        }

        @Test
        @DisplayName("serviceLayer pointcut should be defined")
        void serviceLayer_IsDefined() {
            assertDoesNotThrow(() -> loggingAspect.serviceLayer());
        }

        @Test
        @DisplayName("repositoryLayer pointcut should be defined")
        void repositoryLayer_IsDefined() {
            assertDoesNotThrow(() -> loggingAspect.repositoryLayer());
        }
    }
}
