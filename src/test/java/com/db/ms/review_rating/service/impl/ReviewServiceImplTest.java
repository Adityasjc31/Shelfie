package com.db.ms.review_rating.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.db.ms.review_rating.dto.ReviewCreateDTO;
import com.db.ms.review_rating.dto.ReviewResponseDTO;
import com.db.ms.review_rating.dto.ReviewUpdateDTO;
import com.db.ms.review_rating.exception.DuplicateReviewException;
import com.db.ms.review_rating.exception.InvalidModerationException;
import com.db.ms.review_rating.exception.ReviewNotFoundException;
import com.db.ms.review_rating.exception.UnauthorizedReviewAccessException;
import com.db.ms.review_rating.model.Review;
import com.db.ms.review_rating.model.Review.ReviewStatus;
import com.db.ms.review_rating.repository.ReviewRepository;
import com.db.ms.review_rating.service.impl.ReviewServiceImpl;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ReviewServiceImpl.
 * Uses JUnit 5 and Mockito for testing.
 * 
 * @author Bookstore Development Team
 * @version 1.0
 * @since 2024-12-15
 */
@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private Review testReview;
    private ReviewCreateDTO createDTO;

    @BeforeEach
    void setUp() {
        testReview = Review.builder()
                .reviewId(1L)
                .userId(1001L)
                .bookId(101L)
                .rating(5)
                .comment("Excellent book!")
                .status(ReviewStatus.PENDING)
                .helpfulCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        createDTO = ReviewCreateDTO.builder()
                .userId(1001L)
                .bookId(101L)
                .rating(5)
                .comment("Excellent book!")
                .build();
    }

    @Test
    void createReview_Success() {
        // Arrange
        when(reviewRepository.existsByUserIdAndBookId(1001L, 101L)).thenReturn(false);
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

        // Act
        ReviewResponseDTO result = reviewService.createReview(createDTO);

        // Assert
        assertNotNull(result);
        assertEquals(1001L, result.getUserId());
        assertEquals(101L, result.getBookId());
        assertEquals(5, result.getRating());
        verify(reviewRepository, times(1)).existsByUserIdAndBookId(1001L, 101L);
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void createReview_ThrowsExceptionWhenDuplicate() {
        // Arrange
        when(reviewRepository.existsByUserIdAndBookId(1001L, 101L)).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateReviewException.class, 
                () -> reviewService.createReview(createDTO));
        verify(reviewRepository, times(1)).existsByUserIdAndBookId(1001L, 101L);
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void getReviewById_Success() {
        // Arrange
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(testReview));

        // Act
        ReviewResponseDTO result = reviewService.getReviewById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getReviewId());
        assertEquals(5, result.getRating());
        verify(reviewRepository, times(1)).findById(1L);
    }

    @Test
    void getReviewById_ThrowsExceptionWhenNotFound() {
        // Arrange
        when(reviewRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ReviewNotFoundException.class, 
                () -> reviewService.getReviewById(1L));
        verify(reviewRepository, times(1)).findById(1L);
    }

    @Test
    void getAllReviews_Success() {
        // Arrange
        List<Review> reviews = Arrays.asList(testReview, testReview);
        when(reviewRepository.findAll()).thenReturn(reviews);

        // Act
        List<ReviewResponseDTO> result = reviewService.getAllReviews();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(reviewRepository, times(1)).findAll();
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
        when(reviewRepository.findApprovedReviewsByBookId(101L)).thenReturn(reviews);

        // Act
        List<ReviewResponseDTO> result = reviewService.getApprovedReviewsByBookId(101L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(ReviewStatus.APPROVED, result.get(0).getStatus());
        verify(reviewRepository, times(1)).findApprovedReviewsByBookId(101L);
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

    @Test
    void updateReview_Success() {
        // Arrange
        ReviewUpdateDTO updateDTO = ReviewUpdateDTO.builder()
                .rating(4)
                .comment("Updated comment")
                .build();
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(testReview));
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

        // Act
        ReviewResponseDTO result = reviewService.updateReview(1L, 1001L, updateDTO);

        // Assert
        assertNotNull(result);
        verify(reviewRepository, times(1)).findById(1L);
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void updateReview_ThrowsExceptionWhenUnauthorized() {
        // Arrange
        ReviewUpdateDTO updateDTO = ReviewUpdateDTO.builder()
                .rating(4)
                .comment("Updated comment")
                .build();
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(testReview));

        // Act & Assert
        assertThrows(UnauthorizedReviewAccessException.class, 
                () -> reviewService.updateReview(1L, 9999L, updateDTO));
        verify(reviewRepository, times(1)).findById(1L);
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void approveReview_Success() {
        // Arrange
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(testReview));
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

        // Act
        ReviewResponseDTO result = reviewService.approveReview(1L, 2001L);

        // Assert
        assertNotNull(result);
        verify(reviewRepository, times(1)).findById(1L);
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void rejectReview_Success() {
        // Arrange
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(testReview));
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

        // Act
        ReviewResponseDTO result = reviewService.rejectReview(1L, 2001L, "Inappropriate content");

        // Assert
        assertNotNull(result);
        verify(reviewRepository, times(1)).findById(1L);
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void rejectReview_ThrowsExceptionWhenReasonMissing() {
        // Arrange
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(testReview));

        // Act & Assert
        assertThrows(InvalidModerationException.class, 
                () -> reviewService.rejectReview(1L, 2001L, null));
        verify(reviewRepository, times(1)).findById(1L);
        verify(reviewRepository, never()).save(any(Review.class));
    }

    @Test
    void markReviewAsHelpful_Success() {
        // Arrange
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(testReview));
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

        // Act
        ReviewResponseDTO result = reviewService.markReviewAsHelpful(1L);

        // Assert
        assertNotNull(result);
        verify(reviewRepository, times(1)).findById(1L);
        verify(reviewRepository, times(1)).save(any(Review.class));
    }

    @Test
    void calculateAverageRating_Success() {
        // Arrange
        when(reviewRepository.calculateAverageRating(101L)).thenReturn(4.5);

        // Act
        Double result = reviewService.calculateAverageRating(101L);

        // Assert
        assertNotNull(result);
        assertEquals(4.5, result);
        verify(reviewRepository, times(1)).calculateAverageRating(101L);
    }

    @Test
    void deleteReview_Success() {
        // Arrange
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(testReview));
        doNothing().when(reviewRepository).deleteById(1L);

        // Act
        reviewService.deleteReview(1L, 1001L);

        // Assert
        verify(reviewRepository, times(1)).findById(1L);
        verify(reviewRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteReview_ThrowsExceptionWhenUnauthorized() {
        // Arrange
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(testReview));

        // Act & Assert
        assertThrows(UnauthorizedReviewAccessException.class, 
                () -> reviewService.deleteReview(1L, 9999L));
        verify(reviewRepository, times(1)).findById(1L);
        verify(reviewRepository, never()).deleteById(anyLong());
    }

    @Test
    void deleteReviewByAdmin_Success() {
        // Arrange
        when(reviewRepository.existsById(1L)).thenReturn(true);
        doNothing().when(reviewRepository).deleteById(1L);

        // Act
        reviewService.deleteReviewByAdmin(1L);

        // Assert
        verify(reviewRepository, times(1)).existsById(1L);
        verify(reviewRepository, times(1)).deleteById(1L);
    }
}