// ============================================================================
// FILE: ReviewServiceClientFallbackFactoryTest.java
// ============================================================================
package com.book.management.user.client.fallback;

import com.book.management.user.client.ReviewServiceClient;
import com.book.management.user.dto.ReviewResponseDTO;
import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for ReviewServiceClientFallbackFactory.
 * Tests fallback behavior when Review Service is unavailable.
 *
 * @author Aditya Srivastava
 * @version 1.0
 * @since 2026-01-08
 */
@DisplayName("ReviewServiceClientFallbackFactory Unit Tests")
class ReviewServiceClientFallbackFactoryTest {

    private ReviewServiceClientFallbackFactory fallbackFactory;

    @BeforeEach
    void setUp() {
        fallbackFactory = new ReviewServiceClientFallbackFactory();
    }

    @Test
    @DisplayName("Should create fallback client instance")
    void testCreate_ReturnsFallbackClient() {
        // Given
        RuntimeException cause = new RuntimeException("Service unavailable");

        // When
        ReviewServiceClient fallbackClient = fallbackFactory.create(cause);

        // Then
        assertThat(fallbackClient).isNotNull();
    }

    @Test
    @DisplayName("Should return empty list for getReviewsByUserId fallback")
    void testGetReviewsByUserId_Fallback() {
        // Given
        RuntimeException cause = new RuntimeException("Connection refused");
        ReviewServiceClient fallbackClient = fallbackFactory.create(cause);

        // When
        List<ReviewResponseDTO> reviews = fallbackClient.getReviewsByUserId(123L);

        // Then
        assertThat(reviews).isNotNull();
        assertThat(reviews).isEmpty();
    }

    @Test
    @DisplayName("Should return no content for deleteReview fallback")
    void testDeleteReview_Fallback() {
        // Given
        RuntimeException cause = new RuntimeException("Service down");
        ReviewServiceClient fallbackClient = fallbackFactory.create(cause);

        // When
        ResponseEntity<Void> response = fallbackClient.deleteReview(1L, 123L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("Should return no content for deleteReviewByAdmin fallback")
    void testDeleteReviewByAdmin_Fallback() {
        // Given
        RuntimeException cause = new RuntimeException("Timeout");
        ReviewServiceClient fallbackClient = fallbackFactory.create(cause);

        // When
        ResponseEntity<Void> response = fallbackClient.deleteReviewByAdmin(456L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("Should return no content for deleteReviewsByUserId fallback")
    void testDeleteReviewsByUserId_Fallback() {
        // Given
        RuntimeException cause = new RuntimeException("Connection refused");
        ReviewServiceClient fallbackClient = fallbackFactory.create(cause);

        // When
        ResponseEntity<Void> response = fallbackClient.deleteReviewsByUserId(789L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("Should handle FeignException in getReviewsByUserId fallback")
    void testGetReviewsByUserId_FeignExceptionFallback() {
        // Given
        Request request = Request.create(
                Request.HttpMethod.GET,
                "http://review-service/api/v1/reviews/user/123",
                Collections.emptyMap(),
                null,
                StandardCharsets.UTF_8,
                new RequestTemplate()
        );
        FeignException feignException = new FeignException.ServiceUnavailable(
                "Service Unavailable", request, null, null);

        ReviewServiceClient fallbackClient = fallbackFactory.create(feignException);

        // When
        List<ReviewResponseDTO> reviews = fallbackClient.getReviewsByUserId(123L);

        // Then
        assertThat(reviews).isNotNull();
        assertThat(reviews).isEmpty();
    }

    @Test
    @DisplayName("Should handle FeignException in deleteReviewsByUserId fallback")
    void testDeleteReviewsByUserId_FeignExceptionFallback() {
        // Given
        Request request = Request.create(
                Request.HttpMethod.DELETE,
                "http://review-service/api/v1/reviews/user/123",
                Collections.emptyMap(),
                null,
                StandardCharsets.UTF_8,
                new RequestTemplate()
        );
        FeignException feignException = new FeignException.InternalServerError(
                "Internal Server Error", request, null, null);

        ReviewServiceClient fallbackClient = fallbackFactory.create(feignException);

        // When
        ResponseEntity<Void> response = fallbackClient.deleteReviewsByUserId(123L);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    @DisplayName("Should gracefully handle null parameters")
    void testFallback_NullParameters() {
        // Given
        RuntimeException cause = new RuntimeException("Service down");
        ReviewServiceClient fallbackClient = fallbackFactory.create(cause);

        // When & Then - Should not throw exception
        List<ReviewResponseDTO> reviews = fallbackClient.getReviewsByUserId(null);
        assertThat(reviews).isEmpty();

        ResponseEntity<Void> deleteResponse = fallbackClient.deleteReviewsByUserId(null);
        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
