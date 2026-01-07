package com.book.management.inventory.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for LoggingAspect.
 * Tests AOP logging functionality.
 *
 * @author Aditya Srivastava
 * @version 2.0
 * @since 2024-12-16
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

    @Test
    void logMethodEntry_LogsEntrySuccessfully() {
        // Arrange
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getDeclaringTypeName()).thenReturn("com.test.TestClass");
        when(signature.getName()).thenReturn("testMethod");
        when(joinPoint.getArgs()).thenReturn(new Object[] { "arg1", 123 });

        // Act & Assert - no exception should be thrown
        assertDoesNotThrow(() -> loggingAspect.logMethodEntry(joinPoint));
    }

    @Test
    void logMethodExit_LogsExitSuccessfully() {
        // Arrange
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getDeclaringTypeName()).thenReturn("com.test.TestClass");
        when(signature.getName()).thenReturn("testMethod");

        // Act & Assert - no exception should be thrown
        assertDoesNotThrow(() -> loggingAspect.logMethodExit(joinPoint, "testResult"));
    }

    @Test
    void logMethodExit_WithNullResult_LogsSuccessfully() {
        // Arrange
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getDeclaringTypeName()).thenReturn("com.test.TestClass");
        when(signature.getName()).thenReturn("testMethod");

        // Act & Assert - no exception should be thrown
        assertDoesNotThrow(() -> loggingAspect.logMethodExit(joinPoint, null));
    }

    @Test
    void logExecutionTime_ReturnsResult() throws Throwable {
        // Arrange
        String expectedResult = "test result";
        when(proceedingJoinPoint.getSignature()).thenReturn(signature);
        when(signature.getDeclaringTypeName()).thenReturn("com.test.TestClass");
        when(signature.getName()).thenReturn("testMethod");
        when(proceedingJoinPoint.proceed()).thenReturn(expectedResult);

        // Act
        Object result = loggingAspect.logExecutionTime(proceedingJoinPoint);

        // Assert
        assertEquals(expectedResult, result);
        verify(proceedingJoinPoint, times(1)).proceed();
    }

    @Test
    void logExecutionTime_PropagatesException() throws Throwable {
        // Arrange
        RuntimeException expectedException = new RuntimeException("Test exception");
        when(proceedingJoinPoint.getSignature()).thenReturn(signature);
        when(signature.getDeclaringTypeName()).thenReturn("com.test.TestClass");
        when(signature.getName()).thenReturn("testMethod");
        when(proceedingJoinPoint.proceed()).thenThrow(expectedException);

        // Act & Assert
        RuntimeException thrown = assertThrows(RuntimeException.class,
                () -> loggingAspect.logExecutionTime(proceedingJoinPoint));
        assertEquals("Test exception", thrown.getMessage());
    }

    @Test
    void logException_LogsExceptionSuccessfully() {
        // Arrange
        when(joinPoint.getSignature()).thenReturn(signature);
        when(signature.getDeclaringTypeName()).thenReturn("com.test.TestClass");
        when(signature.getName()).thenReturn("testMethod");
        RuntimeException exception = new RuntimeException("Test exception");

        // Act & Assert - no exception should be thrown
        assertDoesNotThrow(() -> loggingAspect.logException(joinPoint, exception));
    }

    @Test
    void logDatabaseOperation_ReturnsResult() throws Throwable {
        // Arrange
        String expectedResult = "db result";
        when(proceedingJoinPoint.getSignature()).thenReturn(signature);
        when(signature.getName()).thenReturn("findById");
        when(proceedingJoinPoint.getArgs()).thenReturn(new Object[] { 1L });
        when(proceedingJoinPoint.proceed()).thenReturn(expectedResult);

        // Act
        Object result = loggingAspect.logDatabaseOperation(proceedingJoinPoint);

        // Assert
        assertEquals(expectedResult, result);
        verify(proceedingJoinPoint, times(1)).proceed();
    }

    @Test
    void pointcuts_DoNotThrow() {
        // These are just pointcut definitions, they shouldn't throw
        assertDoesNotThrow(() -> loggingAspect.controllerLayer());
        assertDoesNotThrow(() -> loggingAspect.serviceLayer());
        assertDoesNotThrow(() -> loggingAspect.repositoryLayer());
    }
}
