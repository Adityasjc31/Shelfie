package com.book.management.review_rating.filter;

import com.book.management.review_rating.config.GatewaySecurityProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
 *
 * @author Bookstore Development Team
 * @version 1.0
 * @since 2024-12-15
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
    @DisplayName("Should allow request when security is disabled")
    void doFilterInternal_WhenSecurityDisabled_AllowsRequest() throws Exception {
        when(securityProperties.isEnabled()).thenReturn(false);
        when(request.getRequestURI()).thenReturn("/api/v1/reviews");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(response, never()).setStatus(anyInt());
    }

    @Test
    @DisplayName("Should allow request for public endpoints")
    void doFilterInternal_WhenPublicEndpoint_AllowsRequest() throws Exception {
        when(securityProperties.isEnabled()).thenReturn(true);
        when(request.getRequestURI()).thenReturn("/actuator/health");
        when(securityProperties.getPublicPaths()).thenReturn(Arrays.asList("/actuator"));

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(response, never()).setStatus(anyInt());
    }

    @Test
    @DisplayName("Should block request when gateway secret is missing")
    void doFilterInternal_WhenSecretMissing_BlocksRequest() throws Exception {
        when(securityProperties.isEnabled()).thenReturn(true);
        when(request.getRequestURI()).thenReturn("/api/v1/reviews");
        when(securityProperties.getPublicPaths()).thenReturn(Collections.emptyList());
        when(securityProperties.getHeaderName()).thenReturn("X-Gateway-Secret");
        when(request.getHeader("X-Gateway-Secret")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain, never()).doFilter(request, response);
        verify(response).setStatus(403);
    }

    @Test
    @DisplayName("Should block request when gateway secret is empty")
    void doFilterInternal_WhenSecretEmpty_BlocksRequest() throws Exception {
        when(securityProperties.isEnabled()).thenReturn(true);
        when(request.getRequestURI()).thenReturn("/api/v1/reviews");
        when(securityProperties.getPublicPaths()).thenReturn(Collections.emptyList());
        when(securityProperties.getHeaderName()).thenReturn("X-Gateway-Secret");
        when(request.getHeader("X-Gateway-Secret")).thenReturn("");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain, never()).doFilter(request, response);
        verify(response).setStatus(403);
    }

    @Test
    @DisplayName("Should block request when gateway secret is invalid")
    void doFilterInternal_WhenSecretInvalid_BlocksRequest() throws Exception {
        when(securityProperties.isEnabled()).thenReturn(true);
        when(request.getRequestURI()).thenReturn("/api/v1/reviews");
        when(securityProperties.getPublicPaths()).thenReturn(Collections.emptyList());
        when(securityProperties.getHeaderName()).thenReturn("X-Gateway-Secret");
        when(request.getHeader("X-Gateway-Secret")).thenReturn("wrong-token");
        when(securityProperties.getExpectedToken()).thenReturn("correct-token");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        when(response.getWriter()).thenReturn(printWriter);
        when(objectMapper.writeValueAsString(any())).thenReturn("{}");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain, never()).doFilter(request, response);
        verify(response).setStatus(403);
    }

    @Test
    @DisplayName("Should allow request when gateway secret is valid")
    void doFilterInternal_WhenSecretValid_AllowsRequest() throws Exception {
        when(securityProperties.isEnabled()).thenReturn(true);
        when(request.getRequestURI()).thenReturn("/api/v1/reviews");
        when(securityProperties.getPublicPaths()).thenReturn(Collections.emptyList());
        when(securityProperties.getHeaderName()).thenReturn("X-Gateway-Secret");
        when(request.getHeader("X-Gateway-Secret")).thenReturn("correct-token");
        when(securityProperties.getExpectedToken()).thenReturn("correct-token");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(response, never()).setStatus(anyInt());
    }

    @Test
    @DisplayName("Should check multiple public paths")
    void doFilterInternal_WithMultiplePublicPaths_AllowsMatchingRequest() throws Exception {
        when(securityProperties.isEnabled()).thenReturn(true);
        when(request.getRequestURI()).thenReturn("/health/check");
        when(securityProperties.getPublicPaths()).thenReturn(Arrays.asList("/actuator", "/health"));

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }
}
