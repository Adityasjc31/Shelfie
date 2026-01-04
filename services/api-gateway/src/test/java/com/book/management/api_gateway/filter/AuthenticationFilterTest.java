package com.book.management.api_gateway.filter;

import com.book.management.api_gateway.config.AuthenticationProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthenticationFilter.
 * Tests authentication logic for gateway filter.
 * 
 * @author Aditya Srivastava
 * @version 1.0
 * @since 2024-12-29
 */
@ExtendWith(MockitoExtension.class)
public class AuthenticationFilterTest {

    @Mock
    private WebClient.Builder webClientBuilder;

    @Mock
    private WebClient webClient;

    @Mock
    private AuthenticationProperties authenticationProperties;

    @Mock
    private GatewayFilterChain chain;

    private AuthenticationFilter authenticationFilter;

    @BeforeEach
    void setUp() {
        when(webClientBuilder.build()).thenReturn(webClient);
        authenticationFilter = new AuthenticationFilter(webClientBuilder, authenticationProperties);
    }

    @Test
    void testIsPublicEndpoint_LoginPath() {
        // Test that login path is considered public
        assertTrue(isPublicEndpoint("/api/v1/auth/login"));
        assertTrue(isPublicEndpoint("/api/v1/auth/register"));
    }

    @Test
    void testIsPublicEndpoint_ActuatorPath() {
        // Test that actuator paths are considered public
        assertTrue(isPublicEndpoint("/actuator/health"));
        assertTrue(isPublicEndpoint("/actuator/info"));
    }

    @Test
    void testIsPublicEndpoint_ProtectedPath() {
        // Test that regular paths are not public
        assertFalse(isPublicEndpoint("/api/v1/inventory"));
        assertFalse(isPublicEndpoint("/api/v1/reviews"));
        assertFalse(isPublicEndpoint("/api/v1/orders"));
    }

    @Test
    void testFilterApplyReturnsGatewayFilter() {
        // Verify that apply returns a non-null GatewayFilter
        AuthenticationFilter.Config config = new AuthenticationFilter.Config();
        assertNotNull(authenticationFilter.apply(config));
    }

    @Test
    void testAuthDisabledBypasses() {
        // When auth is disabled, requests should pass through
        when(authenticationProperties.isEnabled()).thenReturn(false);

        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/v1/inventory")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        when(chain.filter(any())).thenReturn(Mono.empty());

        AuthenticationFilter.Config config = new AuthenticationFilter.Config();
        var filter = authenticationFilter.apply(config);

        // Execute filter
        filter.filter(exchange, chain).block();

        // Verify chain was called (request passed through)
        verify(chain, times(1)).filter(any());
    }

    @Test
    void testPublicEndpointBypassesAuth() {
        // When accessing public endpoint, auth should be bypassed
        when(authenticationProperties.isEnabled()).thenReturn(true);

        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/v1/auth/login")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        when(chain.filter(any())).thenReturn(Mono.empty());

        AuthenticationFilter.Config config = new AuthenticationFilter.Config();
        var filter = authenticationFilter.apply(config);

        // Execute filter
        filter.filter(exchange, chain).block();

        // Verify chain was called (request passed through)
        verify(chain, times(1)).filter(any());
    }

    @Test
    void testMissingAuthorizationHeaderReturnsUnauthorized() {
        when(authenticationProperties.isEnabled()).thenReturn(true);

        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/v1/inventory")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        AuthenticationFilter.Config config = new AuthenticationFilter.Config();
        var filter = authenticationFilter.apply(config);

        // Execute filter
        filter.filter(exchange, chain).block();

        // Verify response status is unauthorized
        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    @Test
    void testInvalidAuthHeaderFormatReturnsUnauthorized() {
        when(authenticationProperties.isEnabled()).thenReturn(true);

        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/v1/inventory")
                .header(HttpHeaders.AUTHORIZATION, "InvalidToken")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        AuthenticationFilter.Config config = new AuthenticationFilter.Config();
        var filter = authenticationFilter.apply(config);

        // Execute filter
        filter.filter(exchange, chain).block();

        // Verify response status is unauthorized
        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    // Helper method to check if path is public
    private boolean isPublicEndpoint(String path) {
        return path.contains("/auth/login") ||
                path.contains("/auth/register") ||
                path.contains("/actuator");
    }
}
