package com.book.management.book.config;

import feign.RequestTemplate;
import feign.Target;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("GatewaySecretRequestInterceptor Tests")
class GatewaySecretRequestInterceptorTest {

    @Mock
    private GatewaySecurityProperties securityProperties;

    private GatewaySecretRequestInterceptor interceptor;

    @BeforeEach
    void setUp() {
        interceptor = new GatewaySecretRequestInterceptor(securityProperties);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create interceptor with security properties")
        void shouldCreateInterceptorWithSecurityProperties() {
            assertNotNull(interceptor);
        }
    }

    @Nested
    @DisplayName("Apply - When Security Enabled")
    class ApplyWhenSecurityEnabled {

        @Test
        @DisplayName("Should add header when token is configured")
        void shouldAddHeaderWhenTokenIsConfigured() {
            when(securityProperties.isEnabled()).thenReturn(true);
            when(securityProperties.getHeaderName()).thenReturn("X-Gateway-Secret");
            when(securityProperties.getExpectedToken()).thenReturn("my-secret-token");

            RequestTemplate template = createMockRequestTemplate();

            interceptor.apply(template);

            verify(template).header("X-Gateway-Secret", "my-secret-token");
        }

        @Test
        @DisplayName("Should add header with short token")
        void shouldAddHeaderWithShortToken() {
            when(securityProperties.isEnabled()).thenReturn(true);
            when(securityProperties.getHeaderName()).thenReturn("X-Gateway-Secret");
            when(securityProperties.getExpectedToken()).thenReturn("short");

            RequestTemplate template = createMockRequestTemplate();

            interceptor.apply(template);

            verify(template).header("X-Gateway-Secret", "short");
        }

        @Test
        @DisplayName("Should add header with long token")
        void shouldAddHeaderWithLongToken() {
            String longToken = "this-is-a-very-long-secret-token-for-testing";
            when(securityProperties.isEnabled()).thenReturn(true);
            when(securityProperties.getHeaderName()).thenReturn("X-Gateway-Secret");
            when(securityProperties.getExpectedToken()).thenReturn(longToken);

            RequestTemplate template = createMockRequestTemplate();

            interceptor.apply(template);

            verify(template).header("X-Gateway-Secret", longToken);
        }

        @Test
        @DisplayName("Should not add header when token is null")
        void shouldNotAddHeaderWhenTokenIsNull() {
            when(securityProperties.isEnabled()).thenReturn(true);
            when(securityProperties.getExpectedToken()).thenReturn(null);

            RequestTemplate template = createMockRequestTemplate();

            interceptor.apply(template);

            verify(template, never()).header(anyString(), anyString());
        }
    }

    @Nested
    @DisplayName("Apply - When Security Disabled")
    class ApplyWhenSecurityDisabled {

        @Test
        @DisplayName("Should not add header when security is disabled")
        void shouldNotAddHeaderWhenSecurityIsDisabled() {
            when(securityProperties.isEnabled()).thenReturn(false);

            RequestTemplate template = createMockRequestTemplate();

            interceptor.apply(template);

            verify(template, never()).header(anyString(), anyString());
        }
    }

    @Nested
    @DisplayName("Apply - Custom Header Name")
    class ApplyCustomHeaderName {

        @Test
        @DisplayName("Should use custom header name")
        void shouldUseCustomHeaderName() {
            when(securityProperties.isEnabled()).thenReturn(true);
            when(securityProperties.getHeaderName()).thenReturn("Custom-Gateway-Header");
            when(securityProperties.getExpectedToken()).thenReturn("token123");

            RequestTemplate template = createMockRequestTemplate();

            interceptor.apply(template);

            verify(template).header("Custom-Gateway-Header", "token123");
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private RequestTemplate createMockRequestTemplate() {
        RequestTemplate template = mock(RequestTemplate.class);
        Target target = mock(Target.class);
        lenient().when(target.name()).thenReturn("test-service");
        lenient().when(template.feignTarget()).thenReturn(target);
        lenient().when(template.header(anyString(), anyString())).thenReturn(template);
        return template;
    }
}
