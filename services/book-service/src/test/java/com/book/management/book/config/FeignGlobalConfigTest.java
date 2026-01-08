package com.book.management.book.config;

import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FeignGlobalConfig Tests")
class FeignGlobalConfigTest {

    @Mock
    private GatewaySecurityProperties securityProperties;

    private FeignGlobalConfig config;

    @BeforeEach
    void setUp() {
        config = new FeignGlobalConfig(securityProperties);
    }

    @Nested
    @DisplayName("Error Decoder Bean Tests")
    class ErrorDecoderBeanTests {

        @Test
        @DisplayName("Should create error decoder bean")
        void shouldCreateErrorDecoderBean() {
            ErrorDecoder errorDecoder = config.errorDecoder();

            assertNotNull(errorDecoder);
        }

        @Test
        @DisplayName("Should return CustomFeignErrorDecoder instance")
        void shouldReturnCustomFeignErrorDecoderInstance() {
            ErrorDecoder errorDecoder = config.errorDecoder();

            assertInstanceOf(CustomFeignErrorDecoder.class, errorDecoder);
        }

        @Test
        @DisplayName("Should create new instance each time")
        void shouldCreateNewInstanceEachTime() {
            ErrorDecoder errorDecoder1 = config.errorDecoder();
            ErrorDecoder errorDecoder2 = config.errorDecoder();

            assertNotSame(errorDecoder1, errorDecoder2);
        }
    }

    @Nested
    @DisplayName("Request Interceptor Bean Tests")
    class RequestInterceptorBeanTests {

        @Test
        @DisplayName("Should create gateway secret request interceptor bean")
        void shouldCreateGatewaySecretRequestInterceptorBean() {
            RequestInterceptor interceptor = config.gatewaySecretRequestInterceptor();

            assertNotNull(interceptor);
        }

        @Test
        @DisplayName("Should return GatewaySecretRequestInterceptor instance")
        void shouldReturnGatewaySecretRequestInterceptorInstance() {
            RequestInterceptor interceptor = config.gatewaySecretRequestInterceptor();

            assertInstanceOf(GatewaySecretRequestInterceptor.class, interceptor);
        }

        @Test
        @DisplayName("Should create new instance each time")
        void shouldCreateNewInstanceEachTime() {
            RequestInterceptor interceptor1 = config.gatewaySecretRequestInterceptor();
            RequestInterceptor interceptor2 = config.gatewaySecretRequestInterceptor();

            assertNotSame(interceptor1, interceptor2);
        }
    }

    @Nested
    @DisplayName("Configuration Instantiation Tests")
    class ConfigurationInstantiationTests {

        @Test
        @DisplayName("Should create config with security properties")
        void shouldCreateConfigWithSecurityProperties() {
            FeignGlobalConfig newConfig = new FeignGlobalConfig(securityProperties);

            assertNotNull(newConfig);
            assertNotNull(newConfig.errorDecoder());
            assertNotNull(newConfig.gatewaySecretRequestInterceptor());
        }
    }
}
