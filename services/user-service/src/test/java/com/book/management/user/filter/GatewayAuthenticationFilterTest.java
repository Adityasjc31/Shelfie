// ============================================================================
// FILE: GatewayAuthenticationFilterTest.java
// ============================================================================
package com.book.management.user.filter;

import com.book.management.user.config.GatewaySecurityProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests for GatewayAuthenticationFilter.
 * Tests that the filter properly validates gateway authentication.
 *
 * @author Aditya Srivastava
 * @version 1.0
 * @since 2026-01-08
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("GatewayAuthenticationFilter Unit Tests")
class GatewayAuthenticationFilterTest {

    @Mock
    private GatewaySecurityProperties securityProperties;

    @Mock
    private FilterChain filterChain;

    private ObjectMapper objectMapper;
    private GatewayAuthenticationFilter filter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        filter = new GatewayAuthenticationFilter(securityProperties, objectMapper);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    @DisplayName("Should allow request when security is disabled")
    void testDoFilter_SecurityDisabled() throws ServletException, IOException {
        // Given
        when(securityProperties.isEnabled()).thenReturn(false);
        request.setRequestURI("/api/v1/users/1");

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain, times(1)).doFilter(request, response);
        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_OK);
    }

    @Test
    @DisplayName("Should allow request for public endpoints")
    void testDoFilter_PublicEndpoint() throws ServletException, IOException {
        // Given
        when(securityProperties.isEnabled()).thenReturn(true);
        when(securityProperties.getPublicPaths()).thenReturn(Arrays.asList("/actuator", "/health"));
        request.setRequestURI("/actuator/health");

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain, times(1)).doFilter(request, response);
        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_OK);
    }

    @Test
    @DisplayName("Should allow request with valid gateway secret")
    void testDoFilter_ValidGatewaySecret() throws ServletException, IOException {
        // Given
        String validToken = "valid-gateway-secret";
        when(securityProperties.isEnabled()).thenReturn(true);
        when(securityProperties.getPublicPaths()).thenReturn(Collections.emptyList());
        when(securityProperties.getHeaderName()).thenReturn("X-Gateway-Secret");
        when(securityProperties.getExpectedToken()).thenReturn(validToken);

        request.setRequestURI("/api/v1/users/1");
        request.addHeader("X-Gateway-Secret", validToken);

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain, times(1)).doFilter(request, response);
        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_OK);
    }

    @Test
    @DisplayName("Should block request when gateway secret is missing")
    void testDoFilter_MissingGatewaySecret() throws ServletException, IOException {
        // Given
        when(securityProperties.isEnabled()).thenReturn(true);
        when(securityProperties.getPublicPaths()).thenReturn(Collections.emptyList());
        when(securityProperties.getHeaderName()).thenReturn("X-Gateway-Secret");

        request.setRequestURI("/api/v1/users/1");
        request.setRemoteAddr("192.168.1.100");
        // No header added

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain, never()).doFilter(request, response);
        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_FORBIDDEN);
        assertThat(response.getContentAsString()).contains("Direct access not allowed");
    }

    @Test
    @DisplayName("Should block request when gateway secret is empty")
    void testDoFilter_EmptyGatewaySecret() throws ServletException, IOException {
        // Given
        when(securityProperties.isEnabled()).thenReturn(true);
        when(securityProperties.getPublicPaths()).thenReturn(Collections.emptyList());
        when(securityProperties.getHeaderName()).thenReturn("X-Gateway-Secret");

        request.setRequestURI("/api/v1/users/1");
        request.addHeader("X-Gateway-Secret", "");
        request.setRemoteAddr("192.168.1.100");

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain, never()).doFilter(request, response);
        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_FORBIDDEN);
    }

    @Test
    @DisplayName("Should block request when gateway secret is invalid")
    void testDoFilter_InvalidGatewaySecret() throws ServletException, IOException {
        // Given
        String validToken = "valid-gateway-secret";
        String invalidToken = "invalid-token";

        when(securityProperties.isEnabled()).thenReturn(true);
        when(securityProperties.getPublicPaths()).thenReturn(Collections.emptyList());
        when(securityProperties.getHeaderName()).thenReturn("X-Gateway-Secret");
        when(securityProperties.getExpectedToken()).thenReturn(validToken);

        request.setRequestURI("/api/v1/users/1");
        request.addHeader("X-Gateway-Secret", invalidToken);

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain, never()).doFilter(request, response);
        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_FORBIDDEN);
        assertThat(response.getContentAsString()).contains("Invalid gateway credentials");
    }

    @Test
    @DisplayName("Should return JSON error response when blocked")
    void testDoFilter_JsonErrorResponse() throws ServletException, IOException {
        // Given
        when(securityProperties.isEnabled()).thenReturn(true);
        when(securityProperties.getPublicPaths()).thenReturn(Collections.emptyList());
        when(securityProperties.getHeaderName()).thenReturn("X-Gateway-Secret");

        request.setRequestURI("/api/v1/users/1");
        request.setRemoteAddr("127.0.0.1");

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        assertThat(response.getContentType()).isEqualTo("application/json");
        assertThat(response.getContentAsString()).contains("\"status\":403");
        assertThat(response.getContentAsString()).contains("\"error\":\"Forbidden\"");
    }

    @Test
    @DisplayName("Should match public path prefix correctly")
    void testDoFilter_PublicPathPrefixMatching() throws ServletException, IOException {
        // Given
        when(securityProperties.isEnabled()).thenReturn(true);
        when(securityProperties.getPublicPaths()).thenReturn(Arrays.asList("/api/public"));
        request.setRequestURI("/api/public/health");

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("Should not match non-public paths")
    void testDoFilter_NonPublicPath() throws ServletException, IOException {
        // Given
        when(securityProperties.isEnabled()).thenReturn(true);
        when(securityProperties.getPublicPaths()).thenReturn(Arrays.asList("/public"));
        when(securityProperties.getHeaderName()).thenReturn("X-Gateway-Secret");

        request.setRequestURI("/api/v1/users");
        request.setRemoteAddr("127.0.0.1");

        // When
        filter.doFilterInternal(request, response, filterChain);

        // Then
        verify(filterChain, never()).doFilter(request, response);
        assertThat(response.getStatus()).isEqualTo(HttpServletResponse.SC_FORBIDDEN);
    }
}
