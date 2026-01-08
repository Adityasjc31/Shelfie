package com.book.management.book.filter;

import com.book.management.book.config.GatewaySecurityProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("GatewayAuthenticationFilter Tests")
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

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        filter = new GatewayAuthenticationFilter(securityProperties, objectMapper);
    }

    @Nested
    @DisplayName("When security is disabled")
    class WhenSecurityDisabled {

        @Test
        @DisplayName("Should allow request to pass through")
        void shouldAllowRequestToPassThrough() throws ServletException, IOException {
            when(securityProperties.isEnabled()).thenReturn(false);

            filter.doFilterInternal(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
        }
    }

    @Nested
    @DisplayName("When security is enabled")
    class WhenSecurityEnabled {

        @BeforeEach
        void setUp() {
            lenient().when(securityProperties.isEnabled()).thenReturn(true);
            lenient().when(securityProperties.getHeaderName()).thenReturn("X-Gateway-Secret");
            lenient().when(securityProperties.getExpectedToken()).thenReturn("secret-token");
            lenient().when(securityProperties.getPublicPaths()).thenReturn(Collections.emptyList());
        }

        @Nested
        @DisplayName("For public endpoints")
        class ForPublicEndpoints {

            @Test
            @DisplayName("Should allow request to public path")
            void shouldAllowRequestToPublicPath() throws ServletException, IOException {
                when(securityProperties.getPublicPaths()).thenReturn(Arrays.asList("/public", "/health"));
                when(request.getRequestURI()).thenReturn("/public/info");

                filter.doFilterInternal(request, response, filterChain);

                verify(filterChain).doFilter(request, response);
            }

            @Test
            @DisplayName("Should allow request to health endpoint")
            void shouldAllowRequestToHealthEndpoint() throws ServletException, IOException {
                when(securityProperties.getPublicPaths()).thenReturn(Arrays.asList("/actuator/health"));
                when(request.getRequestURI()).thenReturn("/actuator/health");

                filter.doFilterInternal(request, response, filterChain);

                verify(filterChain).doFilter(request, response);
            }
        }

        @Nested
        @DisplayName("For protected endpoints")
        class ForProtectedEndpoints {

            @Test
            @DisplayName("Should block request without gateway secret header")
            void shouldBlockRequestWithoutGatewaySecretHeader() throws ServletException, IOException {
                when(request.getRequestURI()).thenReturn("/api/books");
                when(request.getHeader("X-Gateway-Secret")).thenReturn(null);
                when(request.getRemoteAddr()).thenReturn("192.168.1.1");
                
                StringWriter stringWriter = new StringWriter();
                PrintWriter printWriter = new PrintWriter(stringWriter);
                when(response.getWriter()).thenReturn(printWriter);

                filter.doFilterInternal(request, response, filterChain);

                verify(response).setStatus(403);
                verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
                verify(filterChain, never()).doFilter(request, response);
            }

            @Test
            @DisplayName("Should block request with empty gateway secret header")
            void shouldBlockRequestWithEmptyGatewaySecretHeader() throws ServletException, IOException {
                when(request.getRequestURI()).thenReturn("/api/books");
                when(request.getHeader("X-Gateway-Secret")).thenReturn("");
                when(request.getRemoteAddr()).thenReturn("192.168.1.1");
                
                StringWriter stringWriter = new StringWriter();
                PrintWriter printWriter = new PrintWriter(stringWriter);
                when(response.getWriter()).thenReturn(printWriter);

                filter.doFilterInternal(request, response, filterChain);

                verify(response).setStatus(403);
                verify(filterChain, never()).doFilter(request, response);
            }

            @Test
            @DisplayName("Should block request with invalid gateway secret")
            void shouldBlockRequestWithInvalidGatewaySecret() throws ServletException, IOException {
                when(request.getRequestURI()).thenReturn("/api/books");
                when(request.getHeader("X-Gateway-Secret")).thenReturn("wrong-token");
                
                StringWriter stringWriter = new StringWriter();
                PrintWriter printWriter = new PrintWriter(stringWriter);
                when(response.getWriter()).thenReturn(printWriter);

                filter.doFilterInternal(request, response, filterChain);

                verify(response).setStatus(403);
                verify(filterChain, never()).doFilter(request, response);
            }

            @Test
            @DisplayName("Should allow request with valid gateway secret")
            void shouldAllowRequestWithValidGatewaySecret() throws ServletException, IOException {
                when(request.getRequestURI()).thenReturn("/api/books");
                when(request.getHeader("X-Gateway-Secret")).thenReturn("secret-token");

                filter.doFilterInternal(request, response, filterChain);

                verify(filterChain).doFilter(request, response);
            }
        }
    }
}
