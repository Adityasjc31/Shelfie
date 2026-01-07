package com.book.management.review_rating.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.book.management.review_rating.model.Review.ReviewStatus;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Review entity.
 * Tests entity methods, lifecycle callbacks, and status checking.
 *
 * @author Bookstore Development Team
 * @version 1.0
 * @since 2024-12-15
 */
class ReviewTest {

    private Review review;

    @BeforeEach
    void setUp() {
        review = Review.builder()
                .reviewId(1L)
                .userId(100L)
                .bookId(200L)
                .rating(5)
                .comment("Great book!")
                .status(ReviewStatus.PENDING)
                .build();
    }

    @Nested
    @DisplayName("Status Checking Methods")
    class StatusCheckingTests {

        @Test
        @DisplayName("isApproved should return true when status is APPROVED")
        void isApproved_WhenStatusApproved_ReturnsTrue() {
            review.setStatus(ReviewStatus.APPROVED);
            assertTrue(review.isApproved());
        }

        @Test
        @DisplayName("isApproved should return false when status is PENDING")
        void isApproved_WhenStatusPending_ReturnsFalse() {
            review.setStatus(ReviewStatus.PENDING);
            assertFalse(review.isApproved());
        }

        @Test
        @DisplayName("isApproved should return false when status is REJECTED")
        void isApproved_WhenStatusRejected_ReturnsFalse() {
            review.setStatus(ReviewStatus.REJECTED);
            assertFalse(review.isApproved());
        }

        @Test
        @DisplayName("isPending should return true when status is PENDING")
        void isPending_WhenStatusPending_ReturnsTrue() {
            review.setStatus(ReviewStatus.PENDING);
            assertTrue(review.isPending());
        }

        @Test
        @DisplayName("isPending should return false when status is APPROVED")
        void isPending_WhenStatusApproved_ReturnsFalse() {
            review.setStatus(ReviewStatus.APPROVED);
            assertFalse(review.isPending());
        }

        @Test
        @DisplayName("isRejected should return true when status is REJECTED")
        void isRejected_WhenStatusRejected_ReturnsTrue() {
            review.setStatus(ReviewStatus.REJECTED);
            assertTrue(review.isRejected());
        }

        @Test
        @DisplayName("isRejected should return false when status is APPROVED")
        void isRejected_WhenStatusApproved_ReturnsFalse() {
            review.setStatus(ReviewStatus.APPROVED);
            assertFalse(review.isRejected());
        }
    }

    @Nested
    @DisplayName("Soft Delete Tests")
    class SoftDeleteTests {

        @Test
        @DisplayName("isDeleted should return false by default")
        void isDeleted_DefaultValue_ReturnsFalse() {
            Review newReview = new Review();
            assertFalse(newReview.isDeleted());
        }

        @Test
        @DisplayName("isDeleted should return true when isDeleted is true")
        void isDeleted_WhenTrue_ReturnsTrue() {
            review.setIsDeleted(true);
            assertTrue(review.isDeleted());
        }

        @Test
        @DisplayName("isDeleted should return false when isDeleted is false")
        void isDeleted_WhenFalse_ReturnsFalse() {
            review.setIsDeleted(false);
            assertFalse(review.isDeleted());
        }

        @Test
        @DisplayName("isDeleted should return false when isDeleted is null")
        void isDeleted_WhenNull_ReturnsFalse() {
            review.setIsDeleted(null);
            assertFalse(review.isDeleted());
        }
    }

    @Nested
    @DisplayName("Lifecycle Callback Tests")
    class LifecycleCallbackTests {

        @Test
        @DisplayName("onCreate should set createdAt and updatedAt")
        void onCreate_SetsTimestamps() {
            Review newReview = new Review();
            newReview.onCreate();

            assertNotNull(newReview.getCreatedAt());
            assertNotNull(newReview.getUpdatedAt());
            assertEquals(newReview.getCreatedAt(), newReview.getUpdatedAt());
        }

        @Test
        @DisplayName("onCreate should set isDeleted to false when null")
        void onCreate_SetsIsDeletedToFalseWhenNull() {
            Review newReview = new Review();
            newReview.setIsDeleted(null);
            newReview.onCreate();

            assertFalse(newReview.getIsDeleted());
        }

        @Test
        @DisplayName("onUpdate should update updatedAt")
        void onUpdate_UpdatesTimestamp() {
            review.setCreatedAt(LocalDateTime.now().minusDays(1));
            review.setUpdatedAt(LocalDateTime.now().minusDays(1));

            LocalDateTime beforeUpdate = review.getUpdatedAt();
            review.onUpdate();

            assertNotEquals(beforeUpdate, review.getUpdatedAt());
        }
    }

    @Nested
    @DisplayName("Builder Tests")
    class BuilderTests {

        @Test
        @DisplayName("Builder should create review with all fields")
        void builder_CreatesCompleteReview() {
            Review builtReview = Review.builder()
                    .reviewId(1L)
                    .userId(100L)
                    .bookId(200L)
                    .rating(4)
                    .comment("Good book")
                    .status(ReviewStatus.APPROVED)
                    .moderatedBy(999L)
                    .rejectionReason(null)
                    .isDeleted(false)
                    .build();

            assertEquals(1L, builtReview.getReviewId());
            assertEquals(100L, builtReview.getUserId());
            assertEquals(200L, builtReview.getBookId());
            assertEquals(4, builtReview.getRating());
            assertEquals("Good book", builtReview.getComment());
            assertEquals(ReviewStatus.APPROVED, builtReview.getStatus());
            assertEquals(999L, builtReview.getModeratedBy());
            assertNull(builtReview.getRejectionReason());
            assertFalse(builtReview.getIsDeleted());
        }

        @Test
        @DisplayName("Builder should set default status to PENDING")
        void builder_DefaultStatusIsPending() {
            Review builtReview = Review.builder().build();
            assertEquals(ReviewStatus.PENDING, builtReview.getStatus());
        }

        @Test
        @DisplayName("Builder should set default isDeleted to false")
        void builder_DefaultIsDeletedIsFalse() {
            Review builtReview = Review.builder().build();
            assertFalse(builtReview.getIsDeleted());
        }
    }

    @Nested
    @DisplayName("Getter and Setter Tests")
    class GetterSetterTests {

        @Test
        @DisplayName("Should get and set all fields correctly")
        void gettersAndSetters_WorkCorrectly() {
            Review newReview = new Review();

            newReview.setReviewId(5L);
            newReview.setUserId(50L);
            newReview.setBookId(500L);
            newReview.setRating(3);
            newReview.setComment("Average book");
            newReview.setStatus(ReviewStatus.REJECTED);
            newReview.setModeratedBy(77L);
            newReview.setRejectionReason("Inappropriate content");
            newReview.setIsDeleted(true);
            LocalDateTime now = LocalDateTime.now();
            newReview.setCreatedAt(now);
            newReview.setUpdatedAt(now);

            assertEquals(5L, newReview.getReviewId());
            assertEquals(50L, newReview.getUserId());
            assertEquals(500L, newReview.getBookId());
            assertEquals(3, newReview.getRating());
            assertEquals("Average book", newReview.getComment());
            assertEquals(ReviewStatus.REJECTED, newReview.getStatus());
            assertEquals(77L, newReview.getModeratedBy());
            assertEquals("Inappropriate content", newReview.getRejectionReason());
            assertTrue(newReview.getIsDeleted());
            assertEquals(now, newReview.getCreatedAt());
            assertEquals(now, newReview.getUpdatedAt());
        }
    }

    @Nested
    @DisplayName("ReviewStatus Enum Tests")
    class ReviewStatusEnumTests {

        @Test
        @DisplayName("ReviewStatus should have three values")
        void reviewStatus_HasThreeValues() {
            assertEquals(3, ReviewStatus.values().length);
        }

        @Test
        @DisplayName("ReviewStatus values should be PENDING, APPROVED, REJECTED")
        void reviewStatus_ContainsExpectedValues() {
            assertTrue(java.util.Arrays.asList(ReviewStatus.values()).contains(ReviewStatus.PENDING));
            assertTrue(java.util.Arrays.asList(ReviewStatus.values()).contains(ReviewStatus.APPROVED));
            assertTrue(java.util.Arrays.asList(ReviewStatus.values()).contains(ReviewStatus.REJECTED));
        }

        @Test
        @DisplayName("ReviewStatus valueOf should work correctly")
        void reviewStatus_ValueOfWorks() {
            assertEquals(ReviewStatus.PENDING, ReviewStatus.valueOf("PENDING"));
            assertEquals(ReviewStatus.APPROVED, ReviewStatus.valueOf("APPROVED"));
            assertEquals(ReviewStatus.REJECTED, ReviewStatus.valueOf("REJECTED"));
        }
    }
}
