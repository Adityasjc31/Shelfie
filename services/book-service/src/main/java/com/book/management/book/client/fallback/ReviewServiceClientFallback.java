package com.book.management.book.client.fallback;

import com.book.management.book.client.ReviewServiceClient;
import com.book.management.book.client.dto.BookRatingStatsDTO;
import com.book.management.book.client.dto.RatingDistribution;
import com.book.management.book.client.dto.ReviewResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Fallback factory for ReviewServiceClient.
 * Provides fallback implementations when Review Service is unavailable.
 * 
 * @author Shashwat Srivastava
 * @version 1.0
 * @since 2024-12-15
 */
@Component
@Slf4j
public class ReviewServiceClientFallback implements FallbackFactory<ReviewServiceClient> {

    @Override
    public ReviewServiceClient create(Throwable cause) {
        return new ReviewServiceClient() {

            @Override
            public List<ReviewResponseDTO> getReviewsByBookId(Long bookId) {
                logError("getReviewsByBookId", bookId, cause);
                return Collections.emptyList();
            }

            @Override
            public List<ReviewResponseDTO> getApprovedReviewsByBookId(Long bookId) {
                logError("getApprovedReviewsByBookId", bookId, cause);
                return Collections.emptyList();
            }

            @Override
            public BookRatingStatsDTO getBookRatingStats(Long bookId) {
                logError("getBookRatingStats", bookId, cause);
                return BookRatingStatsDTO.builder()
                        .bookId(bookId)
                        .averageRating(0.0)
                        .totalReviews(0L)
                        .approvedReviews(0L)
                        .pendingReviews(0L)
                        .rejectedReviews(0L)
                        .ratingDistribution(RatingDistribution.builder()
                                .fiveStars(0L)
                                .fourStars(0L)
                                .threeStars(0L)
                                .twoStars(0L)
                                .oneStar(0L)
                                .build())
                        .build();
            }

            @Override
            public Double calculateAverageRating(Long bookId) {
                logError("calculateAverageRating", bookId, cause);
                return 0.0;
            }
        };
    }

    /**
     * Helper method to log errors with consistent formatting.
     * Identifies Security vs Connection issues.
     */
    private void logError(String operation, Long bookId, Throwable cause) {
        if (cause.getMessage() != null && cause.getMessage().contains("401")) {
            log.error("Fallback triggered for {} (bookId={}): Authentication failure (401 Unauthorized).",
                    operation, bookId);
        } else if (cause.getMessage() != null && cause.getMessage().contains("403")) {
            log.error("Fallback triggered for {} (bookId={}): Permission denied (403 Forbidden).",
                    operation, bookId);
        } else {
            log.error("Fallback triggered for {} (bookId={}): Review Service is unreachable. Error: {}",
                    operation, bookId, cause.getMessage());
        }
    }
}
