package com.book.management.review_rating.repository;

import com.book.management.review_rating.model.Review;
import com.book.management.review_rating.model.Review.ReviewStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for ReviewRepository.
 * Uses @DataJpaTest with H2 in-memory database.
 *
 * @author Bookstore Development Team
 * @version 1.0
 * @since 2024-12-15
 */
@DataJpaTest
@TestPropertySource(locations = "classpath:application.properties")
class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

    private Review testReview1;
    private Review testReview2;
    private Review deletedReview;

    @BeforeEach
    void setUp() {
        reviewRepository.deleteAll();

        testReview1 = Review.builder()
                .userId(1001L)
                .bookId(101L)
                .rating(5)
                .comment("Excellent book!")
                .status(ReviewStatus.APPROVED)
                .isDeleted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        testReview2 = Review.builder()
                .userId(1002L)
                .bookId(101L)
                .rating(4)
                .comment("Good book!")
                .status(ReviewStatus.PENDING)
                .isDeleted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        deletedReview = Review.builder()
                .userId(1003L)
                .bookId(101L)
                .rating(3)
                .comment("Deleted review")
                .status(ReviewStatus.APPROVED)
                .isDeleted(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("Basic CRUD Operations")
    class BasicCrudTests {

        @Test
        @DisplayName("Should save and retrieve review")
        void saveAndRetrieve_Success() {
            Review saved = reviewRepository.save(testReview1);

            Optional<Review> found = reviewRepository.findById(saved.getReviewId());

            assertTrue(found.isPresent());
            assertEquals(saved.getReviewId(), found.get().getReviewId());
        }

        @Test
        @DisplayName("Should find all reviews")
        void findAll_ReturnsAllReviews() {
            reviewRepository.save(testReview1);
            reviewRepository.save(testReview2);

            List<Review> all = reviewRepository.findAll();

            assertEquals(2, all.size());
        }
    }

    @Nested
    @DisplayName("Soft Delete Filtering")
    class SoftDeleteFilteringTests {

        @Test
        @DisplayName("findActiveById should return non-deleted review")
        void findActiveById_ReturnsActiveReview() {
            Review saved = reviewRepository.save(testReview1);

            Optional<Review> found = reviewRepository.findActiveById(saved.getReviewId());

            assertTrue(found.isPresent());
        }

        @Test
        @DisplayName("findActiveById should not return deleted review")
        void findActiveById_DoesNotReturnDeletedReview() {
            Review saved = reviewRepository.save(deletedReview);

            Optional<Review> found = reviewRepository.findActiveById(saved.getReviewId());

            assertFalse(found.isPresent());
        }

        @Test
        @DisplayName("findAllActive should exclude deleted reviews")
        void findAllActive_ExcludesDeletedReviews() {
            reviewRepository.save(testReview1);
            reviewRepository.save(testReview2);
            reviewRepository.save(deletedReview);

            List<Review> active = reviewRepository.findAllActive();

            assertEquals(2, active.size());
        }
    }

    @Nested
    @DisplayName("Find By Book Tests")
    class FindByBookTests {

        @Test
        @DisplayName("findByBookId should return reviews for book")
        void findByBookId_ReturnsReviewsForBook() {
            reviewRepository.save(testReview1);
            reviewRepository.save(testReview2);

            List<Review> reviews = reviewRepository.findByBookId(101L);

            assertEquals(2, reviews.size());
        }

        @Test
        @DisplayName("findByBookId should exclude deleted reviews")
        void findByBookId_ExcludesDeletedReviews() {
            reviewRepository.save(testReview1);
            reviewRepository.save(deletedReview);

            List<Review> reviews = reviewRepository.findByBookId(101L);

            assertEquals(1, reviews.size());
        }

        @Test
        @DisplayName("findByBookIdAndStatus should filter by status")
        void findByBookIdAndStatus_FiltersByStatus() {
            reviewRepository.save(testReview1); // APPROVED
            reviewRepository.save(testReview2); // PENDING

            List<Review> approved = reviewRepository.findByBookIdAndStatus(101L, ReviewStatus.APPROVED);

            assertEquals(1, approved.size());
            assertEquals(ReviewStatus.APPROVED, approved.get(0).getStatus());
        }

        @Test
        @DisplayName("findApprovedReviewsByBookId should return only approved")
        void findApprovedReviewsByBookId_ReturnsOnlyApproved() {
            reviewRepository.save(testReview1); // APPROVED
            reviewRepository.save(testReview2); // PENDING

            List<Review> approved = reviewRepository.findApprovedReviewsByBookId(101L);

            assertEquals(1, approved.size());
            assertEquals(ReviewStatus.APPROVED, approved.get(0).getStatus());
        }
    }

    @Nested
    @DisplayName("Find By User Tests")
    class FindByUserTests {

        @Test
        @DisplayName("findByUserId should return reviews by user")
        void findByUserId_ReturnsReviewsByUser() {
            reviewRepository.save(testReview1);
            reviewRepository.save(testReview2);

            List<Review> reviews = reviewRepository.findByUserId(1001L);

            assertEquals(1, reviews.size());
            assertEquals(1001L, reviews.get(0).getUserId());
        }

        @Test
        @DisplayName("findByUserId should exclude deleted reviews")
        void findByUserId_ExcludesDeletedReviews() {
            reviewRepository.save(testReview1);
            Review savedDeleted = Review.builder()
                    .userId(1001L)
                    .bookId(102L)
                    .rating(2)
                    .comment("Deleted")
                    .status(ReviewStatus.PENDING)
                    .isDeleted(true)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            reviewRepository.save(savedDeleted);

            List<Review> reviews = reviewRepository.findByUserId(1001L);

            assertEquals(1, reviews.size());
        }
    }

    @Nested
    @DisplayName("Find By Status Tests")
    class FindByStatusTests {

        @Test
        @DisplayName("findByStatus should return reviews with status")
        void findByStatus_ReturnsReviewsWithStatus() {
            reviewRepository.save(testReview1); // APPROVED
            reviewRepository.save(testReview2); // PENDING

            List<Review> approved = reviewRepository.findByStatus(ReviewStatus.APPROVED);
            List<Review> pending = reviewRepository.findByStatus(ReviewStatus.PENDING);

            assertEquals(1, approved.size());
            assertEquals(1, pending.size());
        }

        @Test
        @DisplayName("findPendingReviews should return only pending")
        void findPendingReviews_ReturnsOnlyPending() {
            reviewRepository.save(testReview1); // APPROVED
            reviewRepository.save(testReview2); // PENDING

            List<Review> pending = reviewRepository.findPendingReviews();

            assertEquals(1, pending.size());
            assertEquals(ReviewStatus.PENDING, pending.get(0).getStatus());
        }
    }

    @Nested
    @DisplayName("Duplicate Check Tests")
    class DuplicateCheckTests {

        @Test
        @DisplayName("existsByUserIdAndBookId should return true when exists")
        void existsByUserIdAndBookId_ReturnsTrueWhenExists() {
            reviewRepository.save(testReview1);

            boolean exists = reviewRepository.existsByUserIdAndBookId(1001L, 101L);

            assertTrue(exists);
        }

        @Test
        @DisplayName("existsByUserIdAndBookId should return false when not exists")
        void existsByUserIdAndBookId_ReturnsFalseWhenNotExists() {
            boolean exists = reviewRepository.existsByUserIdAndBookId(9999L, 101L);

            assertFalse(exists);
        }

        @Test
        @DisplayName("existsByUserIdAndBookId should return false for deleted")
        void existsByUserIdAndBookId_ReturnsFalseForDeleted() {
            reviewRepository.save(deletedReview);

            boolean exists = reviewRepository.existsByUserIdAndBookId(1003L, 101L);

            assertFalse(exists);
        }
    }

    @Nested
    @DisplayName("Count and Average Tests")
    class CountAndAverageTests {

        @Test
        @DisplayName("countByBookId should count reviews for book")
        void countByBookId_CountsReviewsForBook() {
            reviewRepository.save(testReview1);
            reviewRepository.save(testReview2);

            long count = reviewRepository.countByBookId(101L);

            assertEquals(2, count);
        }

        @Test
        @DisplayName("countByBookId should exclude deleted")
        void countByBookId_ExcludesDeleted() {
            reviewRepository.save(testReview1);
            reviewRepository.save(deletedReview);

            long count = reviewRepository.countByBookId(101L);

            assertEquals(1, count);
        }

        @Test
        @DisplayName("countByBookIdAndStatus should count by status")
        void countByBookIdAndStatus_CountsByStatus() {
            reviewRepository.save(testReview1); // APPROVED
            reviewRepository.save(testReview2); // PENDING

            long approved = reviewRepository.countByBookIdAndStatus(101L, ReviewStatus.APPROVED);
            long pending = reviewRepository.countByBookIdAndStatus(101L, ReviewStatus.PENDING);

            assertEquals(1, approved);
            assertEquals(1, pending);
        }

        @Test
        @DisplayName("calculateAverageRating should calculate for approved reviews")
        void calculateAverageRating_CalculatesForApproved() {
            reviewRepository.save(testReview1); // Rating 5, APPROVED
            Review anotherApproved = Review.builder()
                    .userId(1004L)
                    .bookId(101L)
                    .rating(3)
                    .comment("Ok book")
                    .status(ReviewStatus.APPROVED)
                    .isDeleted(false)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            reviewRepository.save(anotherApproved);

            Double average = reviewRepository.calculateAverageRating(101L);

            assertEquals(4.0, average);
        }

        @Test
        @DisplayName("calculateAverageRating should return null when no approved reviews")
        void calculateAverageRating_ReturnsNullWhenNoApproved() {
            reviewRepository.save(testReview2); // PENDING

            Double average = reviewRepository.calculateAverageRating(101L);

            assertNull(average);
        }

        @Test
        @DisplayName("calculateAverageRating should exclude deleted reviews")
        void calculateAverageRating_ExcludesDeleted() {
            reviewRepository.save(testReview1); // Rating 5, APPROVED
            reviewRepository.save(deletedReview); // Rating 3, APPROVED but deleted

            Double average = reviewRepository.calculateAverageRating(101L);

            assertEquals(5.0, average);
        }
    }

    @Nested
    @DisplayName("Soft Delete Tests")
    class SoftDeleteTests {

        @Test
        @DisplayName("softDeleteByUserId should mark reviews as deleted")
        void softDeleteByUserId_MarksReviewsAsDeleted() {
            Review saved = reviewRepository.save(testReview1);

            int deleted = reviewRepository.softDeleteByUserId(1001L);

            assertEquals(1, deleted);

            Optional<Review> found = reviewRepository.findActiveById(saved.getReviewId());
            assertFalse(found.isPresent());
        }

        @Test
        @DisplayName("softDeleteByUserId should return 0 when no reviews")
        void softDeleteByUserId_ReturnsZeroWhenNoReviews() {
            int deleted = reviewRepository.softDeleteByUserId(9999L);

            assertEquals(0, deleted);
        }

        @Test
        @DisplayName("softDeleteByUserId should delete multiple reviews")
        void softDeleteByUserId_DeletesMultiple() {
            Review review1 = Review.builder()
                    .userId(1001L)
                    .bookId(101L)
                    .rating(5)
                    .comment("Great!")
                    .status(ReviewStatus.APPROVED)
                    .isDeleted(false)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            Review review2 = Review.builder()
                    .userId(1001L)
                    .bookId(102L)
                    .rating(4)
                    .comment("Good!")
                    .status(ReviewStatus.PENDING)
                    .isDeleted(false)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            reviewRepository.save(review1);
            reviewRepository.save(review2);

            int deleted = reviewRepository.softDeleteByUserId(1001L);

            assertEquals(2, deleted);
        }
    }
}
