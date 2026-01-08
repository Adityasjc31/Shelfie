// ============================================================================
// FILE: CustomFeignErrorDecoderTest.java
// ============================================================================
package com.book.management.user.config;

import com.book.management.user.exception.UserNotFoundException;
import feign.Request;
import feign.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for CustomFeignErrorDecoder.
 * Tests error decoding from downstream services.
 *
 * @author Aditya Srivastava
 * @version 1.0
 * @since 2026-01-08
 */
@DisplayName("CustomFeignErrorDecoder Unit Tests")
class CustomFeignErrorDecoderTest {

    private CustomFeignErrorDecoder errorDecoder;
    private Request request;

    @BeforeEach
    void setUp() {
        errorDecoder = new CustomFeignErrorDecoder();
        request = Request.create(
                Request.HttpMethod.GET,
                "http://test-service/api/test",
                Collections.emptyMap(),
                null,
                StandardCharsets.UTF_8,
                null
        );
    }

    @Test
    @DisplayName("Should decode 404 error as UserNotFoundException")
    void testDecode_404Error() throws IOException {
        // Given
        String errorBody = "{\"message\": \"Resource not found\", \"status\": 404}";
        Response.Body body = mock(Response.Body.class);
        when(body.asInputStream()).thenReturn(new ByteArrayInputStream(errorBody.getBytes(StandardCharsets.UTF_8)));

        Response response = Response.builder()
                .status(404)
                .reason("Not Found")
                .request(request)
                .headers(Collections.emptyMap())
                .body(body)
                .build();

        // When
        Exception exception = errorDecoder.decode("TestClient#testMethod", response);

        // Then
        assertThat(exception).isInstanceOf(UserNotFoundException.class);
        assertThat(exception.getMessage()).contains("Resource not found");
    }

    @Test
    @DisplayName("Should decode 400 error as RuntimeException")
    void testDecode_400Error() throws IOException {
        // Given
        String errorBody = "{\"message\": \"Bad request data\", \"status\": 400}";
        Response.Body body = mock(Response.Body.class);
        when(body.asInputStream()).thenReturn(new ByteArrayInputStream(errorBody.getBytes(StandardCharsets.UTF_8)));

        Response response = Response.builder()
                .status(400)
                .reason("Bad Request")
                .request(request)
                .headers(Collections.emptyMap())
                .body(body)
                .build();

        // When
        Exception exception = errorDecoder.decode("TestClient#testMethod", response);

        // Then
        assertThat(exception).isInstanceOf(RuntimeException.class);
        assertThat(exception.getMessage()).contains("Bad request data");
    }

    @Test
    @DisplayName("Should decode 500 error as RuntimeException")
    void testDecode_500Error() throws IOException {
        // Given
        String errorBody = "{\"message\": \"Internal server error\", \"status\": 500}";
        Response.Body body = mock(Response.Body.class);
        when(body.asInputStream()).thenReturn(new ByteArrayInputStream(errorBody.getBytes(StandardCharsets.UTF_8)));

        Response response = Response.builder()
                .status(500)
                .reason("Internal Server Error")
                .request(request)
                .headers(Collections.emptyMap())
                .body(body)
                .build();

        // When
        Exception exception = errorDecoder.decode("TestClient#testMethod", response);

        // Then
        assertThat(exception).isInstanceOf(RuntimeException.class);
        assertThat(exception.getMessage()).contains("Internal server error");
    }

    @Test
    @DisplayName("Should handle null response body")
    void testDecode_NullBody() {
        // Given
        Response response = Response.builder()
                .status(500)
                .reason("Internal Server Error")
                .request(request)
                .headers(Collections.emptyMap())
                .body((Response.Body) null)
                .build();

        // When
        Exception exception = errorDecoder.decode("TestClient#testMethod", response);

        // Then
        assertThat(exception).isInstanceOf(RuntimeException.class);
        assertThat(exception.getMessage()).contains("Service communication failure");
    }

    @Test
    @DisplayName("Should handle malformed JSON body")
    void testDecode_MalformedJson() throws IOException {
        // Given
        String malformedBody = "not valid json";
        Response.Body body = mock(Response.Body.class);
        when(body.asInputStream()).thenReturn(new ByteArrayInputStream(malformedBody.getBytes(StandardCharsets.UTF_8)));

        Response response = Response.builder()
                .status(500)
                .reason("Internal Server Error")
                .request(request)
                .headers(Collections.emptyMap())
                .body(body)
                .build();

        // When
        Exception exception = errorDecoder.decode("TestClient#testMethod", response);

        // Then
        assertThat(exception).isInstanceOf(RuntimeException.class);
        assertThat(exception.getMessage()).contains("Service communication failure");
    }

    @Test
    @DisplayName("Should extract message from response body")
    void testDecode_ExtractsMessageFromBody() throws IOException {
        // Given
        String errorBody = "{\"message\": \"Order not found with id: 123\", \"status\": 404, \"error\": \"Not Found\"}";
        Response.Body body = mock(Response.Body.class);
        when(body.asInputStream()).thenReturn(new ByteArrayInputStream(errorBody.getBytes(StandardCharsets.UTF_8)));

        Response response = Response.builder()
                .status(404)
                .reason("Not Found")
                .request(request)
                .headers(Collections.emptyMap())
                .body(body)
                .build();

        // When
        Exception exception = errorDecoder.decode("OrderClient#getOrder", response);

        // Then
        assertThat(exception.getMessage()).contains("Order not found with id: 123");
    }
}
