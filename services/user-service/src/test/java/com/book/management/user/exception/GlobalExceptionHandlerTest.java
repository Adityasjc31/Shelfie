// ============================================================================
// FILE: GlobalExceptionHandlerTest.java
// ============================================================================
package com.book.management.user.exception;

import com.book.management.user.dto.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.util.Arrays;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for GlobalExceptionHandler.
 * Tests all exception handlers return proper responses.
 *
 * @author Aditya Srivastava
 * @version 1.0
 * @since 2026-01-08
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("GlobalExceptionHandler Unit Tests")
class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @Mock
    private WebRequest webRequest;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        when(webRequest.getDescription(false)).thenReturn("uri=/api/v1/users/1");
    }

    @Test
    @DisplayName("Should handle UserNotFoundException with 404 status")
    void testHandleUserNotFoundException() {
        // Given
        UserNotFoundException ex = new UserNotFoundException(123L);

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleUserNotFoundException(ex, webRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(404);
        assertThat(response.getBody().getError()).isEqualTo("Not Found");
        assertThat(response.getBody().getMessage()).contains("123");
        assertThat(response.getBody().getPath()).isEqualTo("/api/v1/users/1");
    }

    @Test
    @DisplayName("Should handle UserAlreadyExistsException with 409 status")
    void testHandleUserAlreadyExistsException() {
        // Given
        UserAlreadyExistsException ex = new UserAlreadyExistsException("test@example.com");

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleUserAlreadyExistsException(ex, webRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(409);
        assertThat(response.getBody().getError()).isEqualTo("Conflict");
        assertThat(response.getBody().getMessage()).contains("test@example.com");
    }

    @Test
    @DisplayName("Should handle InvalidCredentialsException with 401 status")
    void testHandleInvalidCredentialsException() {
        // Given
        InvalidCredentialsException ex = new InvalidCredentialsException();

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleInvalidCredentialsException(ex, webRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(401);
        assertThat(response.getBody().getError()).isEqualTo("Unauthorized");
    }

    @Test
    @DisplayName("Should handle UserInactiveException with 403 status")
    void testHandleUserInactiveException() {
        // Given
        UserInactiveException ex = new UserInactiveException();

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleUserInactiveException(ex, webRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(403);
        assertThat(response.getBody().getError()).isEqualTo("Forbidden");
    }

    @Test
    @DisplayName("Should handle ValidationException with 400 status")
    void testHandleValidationException() {
        // Given
        ValidationException ex = new ValidationException("Invalid role: UNKNOWN");

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationException(ex, webRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getError()).isEqualTo("Bad Request");
        assertThat(response.getBody().getMessage()).contains("Invalid role");
    }

    @Test
    @DisplayName("Should handle MethodArgumentNotValidException with field errors")
    void testHandleMethodArgumentNotValidException() {
        // Given
        BindingResult bindingResult = mock(BindingResult.class);
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);

        FieldError fieldError1 = new FieldError("user", "email", "Email is required");
        FieldError fieldError2 = new FieldError("user", "name", "Name must not be blank");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getAllErrors()).thenReturn(Arrays.asList(fieldError1, fieldError2));

        // When
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleValidationExceptions(ex);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).containsEntry("email", "Email is required");
        assertThat(response.getBody()).containsEntry("name", "Name must not be blank");
    }

    @Test
    @DisplayName("Should handle generic Exception with 500 status")
    void testHandleGlobalException() {
        // Given
        Exception ex = new RuntimeException("Something went wrong");

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGlobalException(ex, webRequest);

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(500);
        assertThat(response.getBody().getError()).isEqualTo("Internal Server Error");
        assertThat(response.getBody().getMessage()).contains("unexpected error");
    }

    @Test
    @DisplayName("Should include timestamp in error response")
    void testErrorResponseContainsTimestamp() {
        // Given
        UserNotFoundException ex = new UserNotFoundException(1L);

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleUserNotFoundException(ex, webRequest);

        // Then
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("Should extract path correctly from WebRequest")
    void testPathExtraction() {
        // Given
        when(webRequest.getDescription(false)).thenReturn("uri=/api/v1/users/register");
        UserAlreadyExistsException ex = new UserAlreadyExistsException("user@test.com");

        // When
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleUserAlreadyExistsException(ex, webRequest);

        // Then
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getPath()).isEqualTo("/api/v1/users/register");
    }
}
