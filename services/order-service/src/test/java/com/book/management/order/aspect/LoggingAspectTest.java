package com.book.management.order.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link LoggingAspect}.
 *
 * Tests the AOP logging aspect that provides centralized logging
 * across controller, service, and repository layers.
 *
 * Test Coverage:
 * - Method entry logging with parameters
 * - Method exit logging with return values
 * - Execution time measurement
 * - Exception handling during execution
 *
 * @author Rehan Ashraf
 * @version 1.0
 * @since 2026-01-07
 */
@ExtendWith(MockitoExtension.class)
class LoggingAspectTest {

    @InjectMocks
    private LoggingAspect loggingAspect;

    @Mock
    private JoinPoint joinPoint;

    @Mock
    private ProceedingJoinPoint proceedingJoinPoint;

    @Mock
    private Signature signature;

    private static final String TEST_CLASS_NAME = "com.book.management.order.service.OrderServiceImpl";
    private static final String TEST_METHOD_NAME = "placeOrder";

    /**
     * Setup common mock behavior before each test.
     */
    @BeforeEach
    void setUp() {
        lenient().when(joinPoint.getSignature()).thenReturn(signature);
        lenient().when(signature.getDeclaringTypeName()).thenReturn(TEST_CLASS_NAME);
        lenient().when(signature.getName()).thenReturn(TEST_METHOD_NAME);
        lenient().when(joinPoint.getArgs()).thenReturn(new Object[]{"arg1", "arg2"});

        lenient().when(proceedingJoinPoint.getSignature()).thenReturn(signature);
    }

    /**
     * Test: Verify that method entry is logged with correct parameters.
     *
     * Expected: Log entry contains class name, method name, and arguments.
     */
    @Test
    void testLogMethodEntry_ShouldLogMethodNameAndArguments() {
        // Act
        loggingAspect.logMethodEntry(joinPoint);

        // Assert
        verify(joinPoint, atLeastOnce()).getSignature();
        verify(signature, atLeastOnce()).getDeclaringTypeName();
        verify(signature, atLeastOnce()).getName();
        verify(joinPoint, atLeastOnce()).getArgs();
    }

    /**
     * Test: Verify that method exit is logged with return value information.
     *
     * Expected: Log entry contains class name, method name, and result type.
     */
    @Test
    void testLogMethodExit_WithNonNullResult_ShouldLogResultType() {
        // Arrange
        String result = "Test Result";

        // Act
        loggingAspect.logMethodExit(joinPoint, result);

        // Assert
        verify(joinPoint, atLeastOnce()).getSignature();
        verify(signature, atLeastOnce()).getDeclaringTypeName();
        verify(signature, atLeastOnce()).getName();
    }

    /**
     * Test: Verify that method exit is logged correctly when result is null.
     *
     * Expected: Log entry indicates void return type.
     */
    @Test
    void testLogMethodExit_WithNullResult_ShouldLogVoid() {
        // Act
        loggingAspect.logMethodExit(joinPoint, null);

        // Assert
        verify(joinPoint, atLeastOnce()).getSignature();
        verify(signature, atLeastOnce()).getDeclaringTypeName();
        verify(signature, atLeastOnce()).getName();
    }

    /**
     * Test: Verify that execution time is measured and logged for successful execution.
     *
     * Expected: Method proceeds normally and execution time is calculated.
     */
    @Test
    void testLogExecutionTime_SuccessfulExecution_ShouldLogTime() throws Throwable {
        // Arrange
        Object expectedResult = "Success";
        when(proceedingJoinPoint.proceed()).thenReturn(expectedResult);

        // Act
        Object result = loggingAspect.logExecutionTime(proceedingJoinPoint);

        // Assert
        assertEquals(expectedResult, result);
        verify(proceedingJoinPoint, times(1)).proceed();
        verify(proceedingJoinPoint, atLeastOnce()).getSignature();
    }

    /**
     * Test: Verify that execution time is logged even when exception occurs.
     *
     * Expected: Exception is re-thrown after logging execution time.
     */
    @Test
    void testLogExecutionTime_WithException_ShouldLogAndRethrow() throws Throwable {
        // Arrange
        RuntimeException testException = new RuntimeException("Test exception");
        when(proceedingJoinPoint.proceed()).thenThrow(testException);

        // Act & Assert
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            loggingAspect.logExecutionTime(proceedingJoinPoint);
        });

        assertEquals("Test exception", thrown.getMessage());
        verify(proceedingJoinPoint, times(1)).proceed();
        verify(proceedingJoinPoint, atLeastOnce()).getSignature();
    }

    /**
     * Test: Verify logging behavior with empty arguments array.
     *
     * Expected: Method entry is logged without errors.
     */
    @Test
    void testLogMethodEntry_WithEmptyArguments_ShouldLogSuccessfully() {
        // Arrange
        when(joinPoint.getArgs()).thenReturn(new Object[]{});

        // Act
        loggingAspect.logMethodEntry(joinPoint);

        // Assert
        verify(joinPoint, atLeastOnce()).getArgs();
    }

    /**
     * Test: Verify logging behavior with multiple arguments.
     *
     * Expected: All arguments are logged correctly.
     */
    @Test
    void testLogMethodEntry_WithMultipleArguments_ShouldLogAll() {
        // Arrange
        Object[] args = {1L, "test", true, 42.0};
        when(joinPoint.getArgs()).thenReturn(args);

        // Act
        loggingAspect.logMethodEntry(joinPoint);

        // Assert
        verify(joinPoint, atLeastOnce()).getArgs();
        verify(signature, atLeastOnce()).getDeclaringTypeName();
        verify(signature, atLeastOnce()).getName();
    }
}

