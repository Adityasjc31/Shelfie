package com.book.management.order.filter;

import com.book.management.order.config.GatewaySecurityProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link GatewayAuthenticationFilter}.
 *
 * Tests the filter that validates requests originate from the API Gateway
 * by checking for the presence and validity of the gateway secret header.
 *
 * Test Coverage:
 * - Valid gateway secret allows request to proceed
 * - Missing gateway secret blocks request with 403
 * - Invalid gateway secret blocks request with 403
 * - Public endpoints bypass authentication
 * - Security disabled allows all requests
 * - Error response format validation
 * - Direct access attempt blocking
 *
 * @author Rehan Ashraf
 * @version 1.0
 * @since 2026-01-07
 */
@ExtendWith(MockitoExtension.class)
class GatewayAuthenticationFilterTest {

    @Mock
    private GatewaySecurityProperties securityProperties;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private GatewayAuthenticationFilter filter;
    private ObjectMapper objectMapper;
    private StringWriter responseWriter;

    private static final String HEADER_NAME = "X-Gateway-Secret";
    private static final String VALID_TOKEN = "valid-gateway-secret-token";
    private static final String INVALID_TOKEN = "invalid-token";
    private static final String TEST_REQUEST_URI = "/api/v1/order/place";

    /**
     * Setup test dependencies before each test.
     */
    @BeforeEach
    void setUp() throws IOException {
        objectMapper = new ObjectMapper();
        filter = new GatewayAuthenticationFilter(securityProperties, objectMapper);
        responseWriter = new StringWriter();

        // Default mock behavior
        lenient().when(request.getRequestURI()).thenReturn(TEST_REQUEST_URI);
        lenient().when(securityProperties.getHeaderName()).thenReturn(HEADER_NAME);
        lenient().when(securityProperties.getExpectedToken()).thenReturn(VALID_TOKEN);
        lenient().when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
    }

    /**
     * Test: Verify request proceeds when valid gateway secret is present.
     *
     * Scenario: Request includes correct gateway secret header.
     * Expected: Filter allows request to continue through filter chain.
     */
    @Test
    void testDoFilterInternal_WithValidSecret_ShouldProceed() throws ServletException, IOException {
        // Arrange
        when(securityProperties.isEnabled()).thenReturn(true);
        when(request.getHeader(HEADER_NAME)).thenReturn(VALID_TOKEN);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        verify(response, never()).setStatus(anyInt());
    }

    /**
     * Test: Verify request is blocked when gateway secret is missing.
     *
     * Scenario: Request does not include gateway secret header.
     * Expected: Filter blocks request with 403 Forbidden.
     */
    @Test
    void testDoFilterInternal_WithMissingSecret_ShouldBlock() throws ServletException, IOException {
        // Arrange
        when(securityProperties.isEnabled()).thenReturn(true);
        when(request.getHeader(HEADER_NAME)).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("192.168.1.100");

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, never()).doFilter(request, response);
        verify(response, times(1)).setStatus(HttpStatus.FORBIDDEN.value());
        verify(response, times(1)).setContentType(MediaType.APPLICATION_JSON_VALUE);
    }

    /**
     * Test: Verify request is blocked when gateway secret is empty.
     *
     * Scenario: Request includes empty gateway secret header.
     * Expected: Filter blocks request with 403 Forbidden.
     */
    @Test
    void testDoFilterInternal_WithEmptySecret_ShouldBlock() throws ServletException, IOException {
        // Arrange
        when(securityProperties.isEnabled()).thenReturn(true);
        when(request.getHeader(HEADER_NAME)).thenReturn("");
        when(request.getRemoteAddr()).thenReturn("192.168.1.100");

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, never()).doFilter(request, response);
        verify(response, times(1)).setStatus(HttpStatus.FORBIDDEN.value());
    }

    /**
     * Test: Verify request is blocked when gateway secret is invalid.
     *
     * Scenario: Request includes incorrect gateway secret value.
     * Expected: Filter blocks request with 403 Forbidden.
     */
    @Test
    void testDoFilterInternal_WithInvalidSecret_ShouldBlock() throws ServletException, IOException {
        // Arrange
        when(securityProperties.isEnabled()).thenReturn(true);
        when(request.getHeader(HEADER_NAME)).thenReturn(INVALID_TOKEN);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, never()).doFilter(request, response);
        verify(response, times(1)).setStatus(HttpStatus.FORBIDDEN.value());
        verify(response, times(1)).setContentType(MediaType.APPLICATION_JSON_VALUE);
    }

    /**
     * Test: Verify public endpoints bypass authentication.
     *
     * Scenario: Request to public endpoint (e.g., actuator health).
     * Expected: Filter allows request without checking gateway secret.
     */
    @Test
    void testDoFilterInternal_WithPublicEndpoint_ShouldBypassAuth() throws ServletException, IOException {
        // Arrange
        String publicPath = "/actuator/health";
        when(securityProperties.isEnabled()).thenReturn(true);
        when(request.getRequestURI()).thenReturn(publicPath);
        when(securityProperties.getPublicPaths()).thenReturn(Arrays.asList("/actuator"));

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        verify(request, never()).getHeader(anyString());
    }

    /**
     * Test: Verify all requests proceed when security is disabled.
     *
     * Scenario: Gateway security is disabled in configuration.
     * Expected: Filter allows all requests without checking header.
     */
    @Test
    void testDoFilterInternal_WithSecurityDisabled_ShouldAllowAll() throws ServletException, IOException {
        // Arrange
        when(securityProperties.isEnabled()).thenReturn(false);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        verify(request, never()).getHeader(anyString());
    }

    /**
     * Test: Verify forbidden response contains proper JSON error structure.
     *
     * Scenario: Direct access attempt without gateway secret.
     * Expected: Response contains timestamp, status, error, and message fields.
     */
    @Test
    void testDoFilterInternal_ForbiddenResponse_ShouldHaveProperJsonStructure() throws ServletException, IOException {
        // Arrange
        when(securityProperties.isEnabled()).thenReturn(true);
        when(request.getHeader(HEADER_NAME)).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("192.168.1.100");

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        String jsonResponse = responseWriter.toString();
        assertTrue(jsonResponse.contains("timestamp"));
        assertTrue(jsonResponse.contains("status"));
        assertTrue(jsonResponse.contains("error"));
        assertTrue(jsonResponse.contains("message"));
        assertTrue(jsonResponse.contains("Direct access not allowed"));
    }

    /**
     * Test: Verify multiple public paths are checked correctly.
     *
     * Scenario: Multiple public endpoints configured.
     * Expected: All public endpoints bypass authentication.
     */
    @Test
    void testDoFilterInternal_WithMultiplePublicPaths_ShouldBypassForAll() throws ServletException, IOException {
        // Arrange
        when(securityProperties.isEnabled()).thenReturn(true);
        when(securityProperties.getPublicPaths()).thenReturn(
            Arrays.asList("/actuator", "/api/v1/public", "/swagger-ui")
        );

        // Test first public path
        when(request.getRequestURI()).thenReturn("/actuator/health");
        filter.doFilterInternal(request, response, filterChain);
        verify(filterChain, times(1)).doFilter(request, response);

        // Test second public path
        reset(filterChain);
        when(request.getRequestURI()).thenReturn("/api/v1/public/status");
        filter.doFilterInternal(request, response, filterChain);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    /**
     * Test: Verify protected endpoint requires authentication even with partial path match.
     *
     * Scenario: Request path similar to public path but not matching.
     * Expected: Authentication is required.
     */
    @Test
    void testDoFilterInternal_WithSimilarButNotPublicPath_ShouldRequireAuth() throws ServletException, IOException {
        // Arrange
        when(securityProperties.isEnabled()).thenReturn(true);
        when(securityProperties.getPublicPaths()).thenReturn(Arrays.asList("/api/v1/public"));
        when(request.getRequestURI()).thenReturn("/api/v1/publicx");
        when(request.getHeader(HEADER_NAME)).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("192.168.1.100");

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, never()).doFilter(request, response);
        verify(response, times(1)).setStatus(HttpStatus.FORBIDDEN.value());
    }

    /**
     * Test: Verify error message for missing secret mentions API Gateway.
     *
     * Scenario: Direct access attempt without gateway secret.
     * Expected: Error message instructs to use API Gateway.
     */
    @Test
    void testDoFilterInternal_MissingSecret_ErrorMessageMentionsGateway() throws ServletException, IOException {
        // Arrange
        when(securityProperties.isEnabled()).thenReturn(true);
        when(request.getHeader(HEADER_NAME)).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("192.168.1.100");

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        String jsonResponse = responseWriter.toString();
        assertTrue(jsonResponse.contains("API Gateway"));
    }

    /**
     * Test: Verify error message for invalid secret is different from missing secret.
     *
     * Scenario: Invalid gateway secret provided.
     * Expected: Error message indicates invalid credentials.
     */
    @Test
    void testDoFilterInternal_InvalidSecret_ErrorMessageIndicatesInvalid() throws ServletException, IOException {
        // Arrange
        when(securityProperties.isEnabled()).thenReturn(true);
        when(request.getHeader(HEADER_NAME)).thenReturn(INVALID_TOKEN);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        String jsonResponse = responseWriter.toString();
        assertTrue(jsonResponse.contains("Invalid gateway credentials"));
    }

    /**
     * Test: Verify case-sensitive token validation.
     *
     * Scenario: Token with different case than expected.
     * Expected: Request is blocked (tokens are case-sensitive).
     */
    @Test
    void testDoFilterInternal_WithCaseDifferentToken_ShouldBlock() throws ServletException, IOException {
        // Arrange
        when(securityProperties.isEnabled()).thenReturn(true);
        when(request.getHeader(HEADER_NAME)).thenReturn(VALID_TOKEN.toUpperCase());

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, never()).doFilter(request, response);
        verify(response, times(1)).setStatus(HttpStatus.FORBIDDEN.value());
    }

    /**
     * Test: Verify filter handles IOException gracefully.
     *
     * Scenario: IOException occurs during response writing.
     * Expected: Exception is propagated (normal filter behavior).
     */
    @Test
    void testDoFilterInternal_WithIOException_ShouldPropagateException() throws IOException {
        // Arrange
        when(securityProperties.isEnabled()).thenReturn(true);
        when(request.getHeader(HEADER_NAME)).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("192.168.1.100");
        when(response.getWriter()).thenThrow(new IOException("Writer error"));

        // Act & Assert
        assertThrows(IOException.class, () -> {
            filter.doFilterInternal(request, response, filterChain);
        });
    }

    /**
     * Test: Verify remote address is logged for blocked requests.
     *
     * Scenario: Direct access attempt from specific IP.
     * Expected: Filter accesses remote address (for logging).
     */
    @Test
    void testDoFilterInternal_BlockedRequest_AccessesRemoteAddress() throws ServletException, IOException {
        // Arrange
        String remoteIp = "10.0.0.50";
        when(securityProperties.isEnabled()).thenReturn(true);
        when(request.getHeader(HEADER_NAME)).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn(remoteIp);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(request, times(1)).getRemoteAddr();
    }
}

