package com.book.management.review_rating.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.book.management.review_rating.client.BookServiceClient;
import com.book.management.review_rating.client.dto.BookResponseDTO;
import com.book.management.review_rating.dto.ReviewCreateDTO;
import com.book.management.review_rating.dto.ReviewModerationDTO;
import com.book.management.review_rating.dto.ReviewResponseDTO;
import com.book.management.review_rating.dto.ReviewUpdateDTO;
import com.book.management.review_rating.exception.DuplicateReviewException;
import com.book.management.review_rating.exception.InvalidModerationException;
import com.book.management.review_rating.exception.InvalidReviewOperationException;
import com.book.management.review_rating.exception.ReviewNotFoundException;
import com.book.management.review_rating.exception.UnauthorizedReviewAccessException;
import com.book.management.review_rating.model.Review;
import com.book.management.review_rating.model.Review.ReviewStatus;
import com.book.management.review_rating.repository.ReviewRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ReviewServiceImpl.
 * Uses JUnit 5 and Mockito for testing.
 * 
 * Tests FeignClient integration with BookServiceClient mock.
 * 
 * @author Bookstore Development Team
 * @version 2.0
 * @since 2026-01-01
 */
@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private BookServiceClient bookServiceClient;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private Review testReview;
    private ReviewCreateDTO createDTO;
    private BookResponseDTO testBook;

    @BeforeEach
    void setUp() {
        testReview = Review.builder()
                .reviewId(1L)
                .userId(1001L)
                .bookId(101L)
                .rating(5)
                .comment("Excellent book!")
                .status(ReviewStatus.PENDING)
                .isDeleted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        createDTO = ReviewCreateDTO.builder()
                .userId(1001L)
                .bookId(101L)
                .rating(5)
                .comment("Excellent book!")
                .build();

        testBook = BookResponseDTO.builder()
                .bookId(101L)
                .bookTitle("Test Book")
                .bookPrice(BigDecimal.valueOf(29.99))
                .stockQuantity(10)
                .build();
    }

    // ============================================================================
    // CREATE Review Tests
    // ============================================================================

    @Nested
    @DisplayName("Create Review Tests")
    class CreateReviewTests {

        @Test
        @DisplayName("Should create review successfully when book exists")
        void createReview_Success() {
            // Arrange - Book exists
            when(bookServiceClient.getBookById(101L)).thenReturn(testBook);
            when(reviewRepository.existsByUserIdAndBookId(1001L, 101L)).thenReturn(false);
            when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

            // Act
            ReviewResponseDTO result = reviewService.createReview(createDTO);

            // Assert
            assertNotNull(result);
            assertEquals(1001L, result.getUserId());
            assertEquals(101L, result.getBookId());
            assertEquals(5, result.getRating());
            verify(bookServiceClient, times(1)).getBookById(101L);
            verify(reviewRepository, times(1)).existsByUserIdAndBookId(1001L, 101L);
            verify(reviewRepository, times(1)).save(any(Review.class));
        }

        @Test
        @DisplayName("Should throw exception when book does not exist")
        void createReview_ThrowsExceptionWhenBookNotFound() {
            // Arrange - Book doesn't exist (FeignClient returns null)
            when(bookServiceClient.getBookById(101L)).thenReturn(null);

            // Act & Assert
            assertThrows(InvalidReviewOperationException.class,
                    () -> reviewService.createReview(createDTO));
            verify(bookServiceClient, times(1)).getBookById(101L);
            verify(reviewRepository, never()).save(any(Review.class));
        }

        @Test
        @DisplayName("Should throw exception when duplicate review exists")
        void createReview_ThrowsExceptionWhenDuplicate() {
            // Arrange
            when(bookServiceClient.getBookById(101L)).thenReturn(testBook);
            when(reviewRepository.existsByUserIdAndBookId(1001L, 101L)).thenReturn(true);

            // Act & Assert
            assertThrows(DuplicateReviewException.class,
                    () -> reviewService.createReview(createDTO));
            verify(reviewRepository, times(1)).existsByUserIdAndBookId(1001L, 101L);
            verify(reviewRepository, never()).save(any(Review.class));
        }
    }

    // ============================================================================
    // READ Review Tests
    // ============================================================================

    @Nested
    @DisplayName("Read Review Tests")
    class ReadReviewTests {

        @Test
        void getReviewById_Success() {
            // Arrange
            when(reviewRepository.findActiveById(1L)).thenReturn(Optional.of(testReview));

            // Act
            ReviewResponseDTO result = reviewService.getReviewById(1L);

            // Assert
            assertNotNull(result);
            assertEquals(1L, result.getReviewId());
            assertEquals(5, result.getRating());
            verify(reviewRepository, times(1)).findActiveById(1L);
        }

        @Test
        void getReviewById_ThrowsExceptionWhenNotFound() {
            // Arrange
            when(reviewRepository.findActiveById(1L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ReviewNotFoundException.class,
                    () -> reviewService.getReviewById(1L));
            verify(reviewRepository, times(1)).findActiveById(1L);
        }

        @Test
        void getAllReviews_Success() {
            // Arrange
            List<Review> reviews = Arrays.asList(testReview, testReview);
            when(reviewRepository.findAllActive()).thenReturn(reviews);

            // Act
            List<ReviewResponseDTO> result = reviewService.getAllReviews();

            // Assert
            assertNotNull(result);
            assertEquals(2, result.size());
            verify(reviewRepository, times(1)).findAllActive();
        }

        @Test
        void getAllReviews_ReturnsEmptyList() {
            // Arrange
            when(reviewRepository.findAllActive()).thenReturn(Collections.emptyList());

            // Act
            List<ReviewResponseDTO> result = reviewService.getAllReviews();

            // Assert
            assertNotNull(result);
            assertTrue(result.isEmpty());
        }

        @Test
        void getReviewsByBookId_Success() {
            // Arrange
            List<Review> reviews = Arrays.asList(testReview);
            when(reviewRepository.findByBookId(101L)).thenReturn(reviews);

            // Act
            List<ReviewResponseDTO> result = reviewService.getReviewsByBookId(101L);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(101L, result.get(0).getBookId());
            verify(reviewRepository, times(1)).findByBookId(101L);
        }

        @Test
        void getApprovedReviewsByBookId_Success() {
            // Arrange
            Review approvedReview = Review.builder()
                    .reviewId(1L)
                    .bookId(101L)
                    .status(ReviewStatus.APPROVED)
                    .rating(5)
                    .build();
            List<Review> reviews = Arrays.asList(approvedReview);
            when(reviewRepository.findByBookIdAndStatus(101L, ReviewStatus.APPROVED)).thenReturn(reviews);

            // Act
            List<ReviewResponseDTO> result = reviewService.getApprovedReviewsByBookId(101L);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(ReviewStatus.APPROVED, result.get(0).getStatus());
            verify(reviewRepository, times(1)).findByBookIdAndStatus(101L, ReviewStatus.APPROVED);
        }

        @Test
        void getReviewsByUserId_Success() {
            // Arrange
            List<Review> reviews = Arrays.asList(testReview);
            when(reviewRepository.findByUserId(1001L)).thenReturn(reviews);

            // Act
            List<ReviewResponseDTO> result = reviewService.getReviewsByUserId(1001L);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(1001L, result.get(0).getUserId());
            verify(reviewRepository, times(1)).findByUserId(1001L);
        }

        @Test
        void getReviewsByStatus_Success() {
            // Arrange
            Review approvedReview = Review.builder()
                    .reviewId(2L)
                    .status(ReviewStatus.APPROVED)
                    .build();
            List<Review> reviews = Arrays.asList(approvedReview);
            when(reviewRepository.findByStatus(ReviewStatus.APPROVED)).thenReturn(reviews);

            // Act
            List<ReviewResponseDTO> result = reviewService.getReviewsByStatus(ReviewStatus.APPROVED);

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(ReviewStatus.APPROVED, result.get(0).getStatus());
            verify(reviewRepository, times(1)).findByStatus(ReviewStatus.APPROVED);
        }

        @Test
        void getPendingReviews_Success() {
            // Arrange
            List<Review> reviews = Arrays.asList(testReview);
            when(reviewRepository.findPendingReviews()).thenReturn(reviews);

            // Act
            List<ReviewResponseDTO> result = reviewService.getPendingReviews();

            // Assert
            assertNotNull(result);
            assertEquals(1, result.size());
            assertEquals(ReviewStatus.PENDING, result.get(0).getStatus());
            verify(reviewRepository, times(1)).findPendingReviews();
        }
    }

    // ============================================================================
    // UPDATE Review Tests
    // ============================================================================

    @Nested
    @DisplayName("Update Review Tests")
    class UpdateReviewTests {

        @Test
        void updateReview_Success() {
            // Arrange
            ReviewUpdateDTO updateDTO = ReviewUpdateDTO.builder()
                    .rating(4)
                    .comment("Updated comment")
                    .build();
            when(reviewRepository.findActiveById(1L)).thenReturn(Optional.of(testReview));
            when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

            // Act
            ReviewResponseDTO result = reviewService.updateReview(1L, 1001L, updateDTO);

            // Assert
            assertNotNull(result);
            verify(reviewRepository, times(1)).findActiveById(1L);
            verify(reviewRepository, times(1)).save(any(Review.class));
        }

        @Test
        void updateReview_ThrowsExceptionWhenNotFound() {
            // Arrange
            ReviewUpdateDTO updateDTO = ReviewUpdateDTO.builder()
                    .rating(4)
                    .comment("Updated comment")
                    .build();
            when(reviewRepository.findActiveById(1L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ReviewNotFoundException.class,
                    () -> reviewService.updateReview(1L, 1001L, updateDTO));
        }

        @Test
        void updateReview_ThrowsExceptionWhenUnauthorized() {
            // Arrange
            ReviewUpdateDTO updateDTO = ReviewUpdateDTO.builder()
                    .rating(4)
                    .comment("Updated comment")
                    .build();
            when(reviewRepository.findActiveById(1L)).thenReturn(Optional.of(testReview));

            // Act & Assert
            assertThrows(UnauthorizedReviewAccessException.class,
                    () -> reviewService.updateReview(1L, 9999L, updateDTO));
            verify(reviewRepository, times(1)).findActiveById(1L);
            verify(reviewRepository, never()).save(any(Review.class));
        }

        @Test
        void updateReview_ThrowsExceptionWhenRejected() {
            // Arrange
            Review rejectedReview = Review.builder()
                    .reviewId(1L)
                    .userId(1001L)
                    .status(ReviewStatus.REJECTED)
                    .build();
            ReviewUpdateDTO updateDTO = ReviewUpdateDTO.builder()
                    .rating(4)
                    .comment("Updated comment")
                    .build();
            when(reviewRepository.findActiveById(1L)).thenReturn(Optional.of(rejectedReview));

            // Act & Assert
            assertThrows(InvalidReviewOperationException.class,
                    () -> reviewService.updateReview(1L, 1001L, updateDTO));
        }

        @Test
        void approveReview_Success() {
            // Arrange
            when(reviewRepository.findActiveById(1L)).thenReturn(Optional.of(testReview));
            when(reviewRepository.save(any(Review.class))).thenReturn(testReview);
            when(reviewRepository.calculateAverageRating(101L)).thenReturn(4.5);
            when(reviewRepository.countByBookIdAndStatus(101L, ReviewStatus.APPROVED)).thenReturn(5L);
            doNothing().when(bookServiceClient).updateBookRating(any(), any());

            // Act
            ReviewResponseDTO result = reviewService.approveReview(1L, 2001L);

            // Assert
            assertNotNull(result);
            verify(reviewRepository, times(1)).findActiveById(1L);
            verify(reviewRepository, times(1)).save(any(Review.class));
            verify(bookServiceClient, times(1)).updateBookRating(any(), any());
        }

        @Test
        void approveReview_ThrowsExceptionWhenNotFound() {
            // Arrange
            when(reviewRepository.findActiveById(1L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ReviewNotFoundException.class,
                    () -> reviewService.approveReview(1L, 2001L));
        }

        @Test
        void rejectReview_Success() {
            // Arrange
            when(reviewRepository.findActiveById(1L)).thenReturn(Optional.of(testReview));
            when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

            // Act
            ReviewResponseDTO result = reviewService.rejectReview(1L, 2001L, "Inappropriate content");

            // Assert
            assertNotNull(result);
            verify(reviewRepository, times(1)).findActiveById(1L);
            verify(reviewRepository, times(1)).save(any(Review.class));
        }

        @Test
        void rejectReview_ThrowsExceptionWhenReasonMissing() {
            // Act & Assert
            assertThrows(InvalidModerationException.class,
                    () -> reviewService.rejectReview(1L, 2001L, null));
            verify(reviewRepository, never()).save(any(Review.class));
        }

        @Test
        void rejectReview_ThrowsExceptionWhenReasonBlank() {
            // Act & Assert
            assertThrows(InvalidModerationException.class,
                    () -> reviewService.rejectReview(1L, 2001L, "   "));
            verify(reviewRepository, never()).save(any(Review.class));
        }

        @Test
        void moderateReview_Approve_Success() {
            // Arrange
            ReviewModerationDTO moderationDTO = ReviewModerationDTO.builder()
                    .status(ReviewStatus.APPROVED)
                    .moderatedBy(2001L)
                    .build();
            when(reviewRepository.findActiveById(1L)).thenReturn(Optional.of(testReview));
            when(reviewRepository.save(any(Review.class))).thenReturn(testReview);
            when(reviewRepository.calculateAverageRating(101L)).thenReturn(4.5);
            when(reviewRepository.countByBookIdAndStatus(101L, ReviewStatus.APPROVED)).thenReturn(5L);
            doNothing().when(bookServiceClient).updateBookRating(any(), any());

            // Act
            ReviewResponseDTO result = reviewService.moderateReview(1L, moderationDTO);

            // Assert
            assertNotNull(result);
            verify(reviewRepository, times(1)).save(any(Review.class));
            verify(bookServiceClient, times(1)).updateBookRating(any(), any());
        }

        @Test
        void moderateReview_Reject_Success() {
            // Arrange
            ReviewModerationDTO moderationDTO = ReviewModerationDTO.builder()
                    .status(ReviewStatus.REJECTED)
                    .moderatedBy(2001L)
                    .rejectionReason("Spam content")
                    .build();
            when(reviewRepository.findActiveById(1L)).thenReturn(Optional.of(testReview));
            when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

            // Act
            ReviewResponseDTO result = reviewService.moderateReview(1L, moderationDTO);

            // Assert
            assertNotNull(result);
            verify(reviewRepository, times(1)).save(any(Review.class));
            // Should NOT update book rating for rejected reviews
            verify(bookServiceClient, never()).updateBookRating(any(), any());
        }

        @Test
        void moderateReview_ThrowsExceptionWhenRejectWithoutReason() {
            // Arrange
            ReviewModerationDTO moderationDTO = ReviewModerationDTO.builder()
                    .status(ReviewStatus.REJECTED)
                    .moderatedBy(2001L)
                    .rejectionReason(null)
                    .build();
            when(reviewRepository.findActiveById(1L)).thenReturn(Optional.of(testReview));

            // Act & Assert
            assertThrows(InvalidModerationException.class,
                    () -> reviewService.moderateReview(1L, moderationDTO));
        }

        @Test
        void moderateReview_ThrowsExceptionWhenRejectWithBlankReason() {
            // Arrange
            ReviewModerationDTO moderationDTO = ReviewModerationDTO.builder()
                    .status(ReviewStatus.REJECTED)
                    .moderatedBy(2001L)
                    .rejectionReason("   ")
                    .build();
            when(reviewRepository.findActiveById(1L)).thenReturn(Optional.of(testReview));

            // Act & Assert
            assertThrows(InvalidModerationException.class,
                    () -> reviewService.moderateReview(1L, moderationDTO));
        }
    }

    // ============================================================================
    // DELETE Review Tests (Soft Delete)
    // ============================================================================

    @Nested
    @DisplayName("Delete Review Tests")
    class DeleteReviewTests {

        @Test
        void deleteReview_Success() {
            // Arrange
            when(reviewRepository.findActiveById(1L)).thenReturn(Optional.of(testReview));
            when(reviewRepository.save(any(Review.class))).thenReturn(testReview);
            when(reviewRepository.calculateAverageRating(101L)).thenReturn(4.0);
            when(reviewRepository.countByBookIdAndStatus(101L, ReviewStatus.APPROVED)).thenReturn(4L);
            doNothing().when(bookServiceClient).updateBookRating(any(), any());

            // Act
            reviewService.deleteReview(1L, 1001L);

            // Assert
            verify(reviewRepository, times(1)).findActiveById(1L);
            verify(reviewRepository, times(1)).save(any(Review.class));
            verify(bookServiceClient, times(1)).updateBookRating(any(), any());
        }

        @Test
        void deleteReview_ThrowsExceptionWhenNotFound() {
            // Arrange
            when(reviewRepository.findActiveById(1L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ReviewNotFoundException.class,
                    () -> reviewService.deleteReview(1L, 1001L));
        }

        @Test
        void deleteReview_ThrowsExceptionWhenUnauthorized() {
            // Arrange
            when(reviewRepository.findActiveById(1L)).thenReturn(Optional.of(testReview));

            // Act & Assert
            assertThrows(UnauthorizedReviewAccessException.class,
                    () -> reviewService.deleteReview(1L, 9999L));
            verify(reviewRepository, times(1)).findActiveById(1L);
            verify(reviewRepository, never()).save(any(Review.class));
        }

        @Test
        void deleteReviewByAdmin_Success() {
            // Arrange
            when(reviewRepository.findActiveById(1L)).thenReturn(Optional.of(testReview));
            when(reviewRepository.save(any(Review.class))).thenReturn(testReview);
            when(reviewRepository.calculateAverageRating(101L)).thenReturn(4.0);
            when(reviewRepository.countByBookIdAndStatus(101L, ReviewStatus.APPROVED)).thenReturn(4L);
            doNothing().when(bookServiceClient).updateBookRating(any(), any());

            // Act
            reviewService.deleteReviewByAdmin(1L);

            // Assert
            verify(reviewRepository, times(1)).findActiveById(1L);
            verify(reviewRepository, times(1)).save(any(Review.class));
        }

        @Test
        void deleteReviewByAdmin_ThrowsExceptionWhenNotFound() {
            // Arrange
            when(reviewRepository.findActiveById(1L)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ReviewNotFoundException.class,
                    () -> reviewService.deleteReviewByAdmin(1L));
        }

        @Test
        void deleteReviewsByUserId_Success() {
            // Arrange
            List<Review> userReviews = Arrays.asList(testReview);
            when(reviewRepository.findByUserId(1001L)).thenReturn(userReviews);
            when(reviewRepository.softDeleteByUserId(1001L)).thenReturn(1);
            when(reviewRepository.calculateAverageRating(101L)).thenReturn(4.0);
            when(reviewRepository.countByBookIdAndStatus(101L, ReviewStatus.APPROVED)).thenReturn(4L);
            doNothing().when(bookServiceClient).updateBookRating(any(), any());

            // Act
            reviewService.deleteReviewsByUserId(1001L);

            // Assert
            verify(reviewRepository, times(1)).findByUserId(1001L);
            verify(reviewRepository, times(1)).softDeleteByUserId(1001L);
            verify(bookServiceClient, times(1)).updateBookRating(any(), any());
        }

        @Test
        void deleteReviewsByUserId_NoReviews() {
            // Arrange
            when(reviewRepository.findByUserId(1001L)).thenReturn(Collections.emptyList());

            // Act
            reviewService.deleteReviewsByUserId(1001L);

            // Assert
            verify(reviewRepository, times(1)).findByUserId(1001L);
            verify(reviewRepository, never()).softDeleteByUserId(anyLong());
        }

        @Test
        void deleteReviewsByUserId_MultipleBooks() {
            // Arrange
            Review review1 = Review.builder().reviewId(1L).userId(1001L).bookId(101L).build();
            Review review2 = Review.builder().reviewId(2L).userId(1001L).bookId(102L).build();
            List<Review> userReviews = Arrays.asList(review1, review2);

            when(reviewRepository.findByUserId(1001L)).thenReturn(userReviews);
            when(reviewRepository.softDeleteByUserId(1001L)).thenReturn(2);
            when(reviewRepository.calculateAverageRating(anyLong())).thenReturn(4.0);
            when(reviewRepository.countByBookIdAndStatus(anyLong(), any())).thenReturn(4L);
            doNothing().when(bookServiceClient).updateBookRating(any(), any());

            // Act
            reviewService.deleteReviewsByUserId(1001L);

            // Assert
            verify(reviewRepository, times(1)).softDeleteByUserId(1001L);
            // Should update ratings for both books
            verify(bookServiceClient, times(2)).updateBookRating(any(), any());
        }
    }

    // ============================================================================
    // Book Service Rating Update Error Handling
    // ============================================================================

    @Nested
    @DisplayName("Book Service Error Handling")
    class BookServiceErrorHandlingTests {

        @Test
        void approveReview_ContinuesWhenBookServiceFails() {
            // Arrange
            when(reviewRepository.findActiveById(1L)).thenReturn(Optional.of(testReview));
            when(reviewRepository.save(any(Review.class))).thenReturn(testReview);
            when(reviewRepository.calculateAverageRating(101L)).thenReturn(4.5);
            when(reviewRepository.countByBookIdAndStatus(101L, ReviewStatus.APPROVED)).thenReturn(5L);
            doThrow(new RuntimeException("Book Service unavailable"))
                    .when(bookServiceClient).updateBookRating(any(), any());

            // Act - should not throw exception even if book service fails
            ReviewResponseDTO result = reviewService.approveReview(1L, 2001L);

            // Assert
            assertNotNull(result);
            verify(reviewRepository, times(1)).save(any(Review.class));
        }

        @Test
        void deleteReview_ContinuesWhenBookServiceFails() {
            // Arrange
            when(reviewRepository.findActiveById(1L)).thenReturn(Optional.of(testReview));
            when(reviewRepository.save(any(Review.class))).thenReturn(testReview);
            when(reviewRepository.calculateAverageRating(101L)).thenReturn(4.0);
            when(reviewRepository.countByBookIdAndStatus(101L, ReviewStatus.APPROVED)).thenReturn(4L);
            doThrow(new RuntimeException("Book Service unavailable"))
                    .when(bookServiceClient).updateBookRating(any(), any());

            // Act - should not throw exception even if book service fails
            assertDoesNotThrow(() -> reviewService.deleteReview(1L, 1001L));

            // Assert
            verify(reviewRepository, times(1)).save(any(Review.class));
        }

        @Test
        void approveReview_HandlesNullAverageRating() {
            // Arrange
            when(reviewRepository.findActiveById(1L)).thenReturn(Optional.of(testReview));
            when(reviewRepository.save(any(Review.class))).thenReturn(testReview);
            when(reviewRepository.calculateAverageRating(101L)).thenReturn(null);
            when(reviewRepository.countByBookIdAndStatus(101L, ReviewStatus.APPROVED)).thenReturn(0L);
            doNothing().when(bookServiceClient).updateBookRating(any(), any());

            // Act
            ReviewResponseDTO result = reviewService.approveReview(1L, 2001L);

            // Assert
            assertNotNull(result);
            verify(bookServiceClient, times(1)).updateBookRating(any(), any());
        }
    }
}