package com.book.management.book.client.fallback;

import com.book.management.book.client.ReviewServiceClient;
import com.book.management.book.client.dto.BookRatingStatsDTO;
import com.book.management.book.client.dto.ReviewResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ReviewServiceClientFallback Tests")
class ReviewServiceClientFallbackTest {

    private ReviewServiceClientFallback fallbackFactory;

    @BeforeEach
    void setUp() {
        fallbackFactory = new ReviewServiceClientFallback();
    }

    @Nested
    @DisplayName("Create Fallback Tests")
    class CreateFallbackTests {

        @Test
        @DisplayName("Should create fallback client")
        void shouldCreateFallbackClient() {
            ReviewServiceClient fallbackClient = fallbackFactory.create(new RuntimeException("Service down"));

            assertNotNull(fallbackClient);
        }

        @Test
        @DisplayName("Should create different instances for different causes")
        void shouldCreateDifferentInstancesForDifferentCauses() {
            ReviewServiceClient client1 = fallbackFactory.create(new RuntimeException("Error 1"));
            ReviewServiceClient client2 = fallbackFactory.create(new RuntimeException("Error 2"));

            assertNotSame(client1, client2);
        }
    }

    @Nested
    @DisplayName("GetReviewsByBookId Fallback Tests")
    class GetReviewsByBookIdFallbackTests {

        @Test
        @DisplayName("Should return empty list for getReviewsByBookId")
        void shouldReturnEmptyListForGetReviewsByBookId() {
            ReviewServiceClient fallbackClient = fallbackFactory.create(new RuntimeException("Connection refused"));

            List<ReviewResponseDTO> result = fallbackClient.getReviewsByBookId(1L);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should log 401 error for getReviewsByBookId")
        void shouldLog401ErrorForGetReviewsByBookId() {
            ReviewServiceClient fallbackClient = fallbackFactory.create(new RuntimeException("401 Unauthorized"));

            List<ReviewResponseDTO> result = fallbackClient.getReviewsByBookId(2L);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should log 403 error for getReviewsByBookId")
        void shouldLog403ErrorForGetReviewsByBookId() {
            ReviewServiceClient fallbackClient = fallbackFactory.create(new RuntimeException("403 Forbidden"));

            List<ReviewResponseDTO> result = fallbackClient.getReviewsByBookId(3L);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("GetApprovedReviewsByBookId Fallback Tests")
    class GetApprovedReviewsByBookIdFallbackTests {

        @Test
        @DisplayName("Should return empty list for getApprovedReviewsByBookId")
        void shouldReturnEmptyListForGetApprovedReviewsByBookId() {
            ReviewServiceClient fallbackClient = fallbackFactory.create(new RuntimeException("Network error"));

            List<ReviewResponseDTO> result = fallbackClient.getApprovedReviewsByBookId(10L);

            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("Should log 401 error for getApprovedReviewsByBookId")
        void shouldLog401ErrorForGetApprovedReviewsByBookId() {
            ReviewServiceClient fallbackClient = fallbackFactory.create(new RuntimeException("401 authentication failed"));

            List<ReviewResponseDTO> result = fallbackClient.getApprovedReviewsByBookId(20L);

            assertNotNull(result);
        }

        @Test
        @DisplayName("Should log 403 error for getApprovedReviewsByBookId")
        void shouldLog403ErrorForGetApprovedReviewsByBookId() {
            ReviewServiceClient fallbackClient = fallbackFactory.create(new RuntimeException("403 access denied"));

            List<ReviewResponseDTO> result = fallbackClient.getApprovedReviewsByBookId(30L);

            assertNotNull(result);
        }
    }

    @Nested
    @DisplayName("GetBookRatingStats Fallback Tests")
    class GetBookRatingStatsFallbackTests {

        @Test
        @DisplayName("Should return default rating stats for getBookRatingStats")
        void shouldReturnDefaultRatingStatsForGetBookRatingStats() {
            ReviewServiceClient fallbackClient = fallbackFactory.create(new RuntimeException("Service unavailable"));

            BookRatingStatsDTO result = fallbackClient.getBookRatingStats(100L);

            assertNotNull(result);
            assertEquals(100L, result.getBookId());
            assertEquals(0.0, result.getAverageRating());
            assertEquals(0L, result.getTotalReviews());
            assertEquals(0L, result.getApprovedReviews());
            assertEquals(0L, result.getPendingReviews());
            assertEquals(0L, result.getRejectedReviews());
        }

        @Test
        @DisplayName("Should return rating distribution with zeros")
        void shouldReturnRatingDistributionWithZeros() {
            ReviewServiceClient fallbackClient = fallbackFactory.create(new RuntimeException("Timeout"));

            BookRatingStatsDTO result = fallbackClient.getBookRatingStats(200L);

            assertNotNull(result.getRatingDistribution());
            assertEquals(0L, result.getRatingDistribution().getFiveStars());
            assertEquals(0L, result.getRatingDistribution().getFourStars());
            assertEquals(0L, result.getRatingDistribution().getThreeStars());
            assertEquals(0L, result.getRatingDistribution().getTwoStars());
            assertEquals(0L, result.getRatingDistribution().getOneStar());
        }

        @Test
        @DisplayName("Should log 401 error for getBookRatingStats")
        void shouldLog401ErrorForGetBookRatingStats() {
            ReviewServiceClient fallbackClient = fallbackFactory.create(new RuntimeException("401 unauthorized"));

            BookRatingStatsDTO result = fallbackClient.getBookRatingStats(300L);

            assertNotNull(result);
            assertEquals(300L, result.getBookId());
        }

        @Test
        @DisplayName("Should log 403 error for getBookRatingStats")
        void shouldLog403ErrorForGetBookRatingStats() {
            ReviewServiceClient fallbackClient = fallbackFactory.create(new RuntimeException("403 forbidden"));

            BookRatingStatsDTO result = fallbackClient.getBookRatingStats(400L);

            assertNotNull(result);
            assertEquals(400L, result.getBookId());
        }
    }

    @Nested
    @DisplayName("CalculateAverageRating Fallback Tests")
    class CalculateAverageRatingFallbackTests {

        @Test
        @DisplayName("Should return 0.0 for calculateAverageRating")
        void shouldReturnZeroForCalculateAverageRating() {
            ReviewServiceClient fallbackClient = fallbackFactory.create(new RuntimeException("Service down"));

            Double result = fallbackClient.calculateAverageRating(50L);

            assertNotNull(result);
            assertEquals(0.0, result);
        }

        @Test
        @DisplayName("Should log 401 error for calculateAverageRating")
        void shouldLog401ErrorForCalculateAverageRating() {
            ReviewServiceClient fallbackClient = fallbackFactory.create(new RuntimeException("401 error"));

            Double result = fallbackClient.calculateAverageRating(60L);

            assertEquals(0.0, result);
        }

        @Test
        @DisplayName("Should log 403 error for calculateAverageRating")
        void shouldLog403ErrorForCalculateAverageRating() {
            ReviewServiceClient fallbackClient = fallbackFactory.create(new RuntimeException("403 error"));

            Double result = fallbackClient.calculateAverageRating(70L);

            assertEquals(0.0, result);
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle null message in cause")
        void shouldHandleNullMessageInCause() {
            ReviewServiceClient fallbackClient = fallbackFactory.create(new RuntimeException());

            List<ReviewResponseDTO> reviews = fallbackClient.getReviewsByBookId(500L);
            Double rating = fallbackClient.calculateAverageRating(500L);
            BookRatingStatsDTO stats = fallbackClient.getBookRatingStats(500L);

            assertNotNull(reviews);
            assertTrue(reviews.isEmpty());
            assertEquals(0.0, rating);
            assertNotNull(stats);
        }

        @Test
        @DisplayName("Should handle empty message in cause")
        void shouldHandleEmptyMessageInCause() {
            ReviewServiceClient fallbackClient = fallbackFactory.create(new RuntimeException(""));

            List<ReviewResponseDTO> reviews = fallbackClient.getReviewsByBookId(600L);

            assertNotNull(reviews);
            assertTrue(reviews.isEmpty());
        }

        @Test
        @DisplayName("Should handle different exception types")
        void shouldHandleDifferentExceptionTypes() {
            ReviewServiceClient npeFallback = fallbackFactory.create(new NullPointerException("null ref"));
            ReviewServiceClient iseFallback = fallbackFactory.create(new IllegalStateException("bad state"));

            assertNotNull(npeFallback.getReviewsByBookId(700L));
            assertNotNull(iseFallback.calculateAverageRating(800L));
        }
    }
}
