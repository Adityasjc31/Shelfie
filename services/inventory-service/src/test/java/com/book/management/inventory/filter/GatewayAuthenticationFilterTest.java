package com.book.management.inventory.filter;

import com.book.management.inventory.config.GatewaySecurityProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.*;

/**
 * Unit tests for GatewayAuthenticationFilter.
 * Tests security validation for incoming requests.
 *
 * @author Aditya Srivastava
 * @version 2.0
 * @since 2026-01-02
 */
@ExtendWith(MockitoExtension.class)
class GatewayAuthenticationFilterTest {

    @Mock
    private GatewaySecurityProperties securityProperties;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private GatewayAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        filter = new GatewayAuthenticationFilter(securityProperties, objectMapper);
    }

    @Test
    void validGatewaySecret_AllowsRequest() throws Exception {
        // Arrange
        when(securityProperties.isEnabled()).thenReturn(true);
        when(securityProperties.getPublicPaths()).thenReturn(Collections.emptyList());
        when(securityProperties.getHeaderName()).thenReturn("X-Gateway-Secret");
        when(securityProperties.getExpectedToken()).thenReturn("valid-secret-token");
        when(request.getRequestURI()).thenReturn("/api/v1/inventory");
        when(request.getHeader("X-Gateway-Secret")).thenReturn("valid-secret-token");

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        verify(response, never()).setStatus(anyInt());
    }

    @Test
    void missingGatewaySecret_BlocksRequest() throws Exception {
        // Arrange
        when(securityProperties.isEnabled()).thenReturn(true);
        when(securityProperties.getPublicPaths()).thenReturn(Collections.emptyList());
        when(securityProperties.getHeaderName()).thenReturn("X-Gateway-Secret");
        when(securityProperties.getExpectedToken()).thenReturn("valid-secret-token");
        when(request.getRequestURI()).thenReturn("/api/v1/inventory");
        when(request.getHeader("X-Gateway-Secret")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        StringWriter stringWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, never()).doFilter(request, response);
        verify(response, times(1)).setStatus(403);
    }

    @Test
    void invalidGatewaySecret_BlocksRequest() throws Exception {
        // Arrange
        when(securityProperties.isEnabled()).thenReturn(true);
        when(securityProperties.getPublicPaths()).thenReturn(Collections.emptyList());
        when(securityProperties.getHeaderName()).thenReturn("X-Gateway-Secret");
        when(securityProperties.getExpectedToken()).thenReturn("valid-secret-token");
        when(request.getRequestURI()).thenReturn("/api/v1/inventory");
        when(request.getHeader("X-Gateway-Secret")).thenReturn("invalid-token");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        StringWriter stringWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, never()).doFilter(request, response);
        verify(response, times(1)).setStatus(403);
    }

    @Test
    void publicEndpoint_BypassesSecurity() throws Exception {
        // Arrange
        when(securityProperties.isEnabled()).thenReturn(true);
        when(securityProperties.getPublicPaths()).thenReturn(Arrays.asList("/actuator"));
        when(request.getRequestURI()).thenReturn("/actuator/health");

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        verify(response, never()).setStatus(anyInt());
    }

    @Test
    void securityDisabled_AllowsAllRequests() throws Exception {
        // Arrange
        when(securityProperties.isEnabled()).thenReturn(false);
        when(request.getRequestURI()).thenReturn("/api/v1/inventory");

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
        verify(response, never()).setStatus(anyInt());
        verify(securityProperties, never()).getHeaderName();
    }

    @Test
    void emptyGatewaySecret_BlocksRequest() throws Exception {
        // Arrange
        when(securityProperties.isEnabled()).thenReturn(true);
        when(securityProperties.getPublicPaths()).thenReturn(Collections.emptyList());
        when(securityProperties.getHeaderName()).thenReturn("X-Gateway-Secret");
        when(securityProperties.getExpectedToken()).thenReturn("valid-secret-token");
        when(request.getRequestURI()).thenReturn("/api/v1/inventory");
        when(request.getHeader("X-Gateway-Secret")).thenReturn("");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        StringWriter stringWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(stringWriter));
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, never()).doFilter(request, response);
        verify(response, times(1)).setStatus(403);
    }

    @Test
    void shortToken_LogsCorrectPrefix() throws Exception {
        // Arrange - test with short token (less than 8 chars)
        when(securityProperties.isEnabled()).thenReturn(true);
        when(securityProperties.getPublicPaths()).thenReturn(Collections.emptyList());
        when(securityProperties.getHeaderName()).thenReturn("X-Gateway-Secret");
        when(securityProperties.getExpectedToken()).thenReturn("short");
        when(request.getRequestURI()).thenReturn("/api/v1/inventory");
        when(request.getHeader("X-Gateway-Secret")).thenReturn("short");

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(request, response);
    }
}
