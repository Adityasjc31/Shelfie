package com.book.management.review_rating.service.impl;

import com.book.management.review_rating.client.BookServiceClient;
import com.book.management.review_rating.client.dto.BookRatingUpdateRequest;
import com.book.management.review_rating.client.dto.BookResponseDTO;
import com.book.management.review_rating.dto.*;
import com.book.management.review_rating.exception.*;
import com.book.management.review_rating.model.Review;
import com.book.management.review_rating.model.Review.ReviewStatus;
import com.book.management.review_rating.repository.ReviewRepository;
import com.book.management.review_rating.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of ReviewService for Microservices Architecture.
 * 
 * Microservices Patterns Applied:
 * 1. Service-to-Service Communication: Uses FeignClient (BookServiceClient)
 * 2. Database per Service: Own review database
 * 3. Transaction Management: @Transactional for data consistency
 * 4. AOP Logging: Automatic logging via LoggingAspect
 * 5. Exception Handling: Consistent error responses
 * 6. Circuit Breaker: Fallback when Book Service is unavailable
 * 
 * Dependencies:
 * - Book Service: For book validation (via FeignClient)
 * - Review Database: For review persistence (via JPA)
 * 
 * Inter-Service Communication Flow:
 * 1. Create Review: Validates book existence via Book Service
 * 2. Approve/Delete Review: Updates book rating in Book Service
 * 
 * @author Aditya Srivastava
 * @version 3.0
 * @since 2026-01-01
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookServiceClient bookServiceClient;

    // ============================================================================
    // CREATE Operations
    // ============================================================================

    @Override
    public ReviewResponseDTO createReview(ReviewCreateDTO createDTO) {
        // Validate book existence via Book Service (FeignClient)
        BookResponseDTO book = bookServiceClient.getBookById(createDTO.getBookId());

        if (book == null) {
            throw new InvalidReviewOperationException(
                    String.format("Cannot create review: Book with ID %d does not exist or Book Service is unavailable",
                            createDTO.getBookId()));
        }

        // Check for duplicate reviews
        if (reviewRepository.existsByUserIdAndBookId(
                createDTO.getUserId(), createDTO.getBookId())) {
            throw new DuplicateReviewException(createDTO.getUserId(), createDTO.getBookId());
        }

        // Create and save review
        Review review = Review.builder()
                .userId(createDTO.getUserId())
                .bookId(createDTO.getBookId())
                .rating(createDTO.getRating())
                .comment(createDTO.getComment())
                .status(ReviewStatus.PENDING)
                .build();

        Review savedReview = reviewRepository.save(review);
        log.info("Created new review ID: {} for book ID: {} by user ID: {}",
                savedReview.getReviewId(), savedReview.getBookId(), savedReview.getUserId());

        return mapToResponseDTO(savedReview);
    }

    // ============================================================================
    // READ Operations
    // ============================================================================

    @Override
    @Transactional(readOnly = true)
    public ReviewResponseDTO getReviewById(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));
        return mapToResponseDTO(review);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponseDTO> getAllReviews() {
        return reviewRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponseDTO> getReviewsByBookId(Long bookId) {
        return reviewRepository.findByBookId(bookId).stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponseDTO> getApprovedReviewsByBookId(Long bookId) {
        return reviewRepository.findByBookIdAndStatus(bookId, ReviewStatus.APPROVED).stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponseDTO> getReviewsByUserId(Long userId) {
        return reviewRepository.findByUserId(userId).stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponseDTO> getReviewsByStatus(ReviewStatus status) {
        return reviewRepository.findByStatus(status).stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponseDTO> getPendingReviews() {
        return reviewRepository.findPendingReviews().stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    // ============================================================================
    // UPDATE Operations
    // ============================================================================

    @Override
    public ReviewResponseDTO updateReview(Long reviewId, Long userId, ReviewUpdateDTO updateDTO) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));

        if (!review.getUserId().equals(userId)) {
            throw new UnauthorizedReviewAccessException(userId, reviewId);
        }

        if (review.isRejected()) {
            throw new InvalidReviewOperationException(
                    "Cannot update rejected review. Please create a new review instead.");
        }

        review.setRating(updateDTO.getRating());
        review.setComment(updateDTO.getComment());
        review.setStatus(ReviewStatus.PENDING);

        return mapToResponseDTO(reviewRepository.save(review));
    }

    @Override
    public ReviewResponseDTO moderateReview(Long reviewId, ReviewModerationDTO moderationDTO) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));

        if (moderationDTO.getStatus() == ReviewStatus.REJECTED &&
                (moderationDTO.getRejectionReason() == null ||
                        moderationDTO.getRejectionReason().isBlank())) {
            throw new InvalidModerationException(
                    "Rejection reason is required when rejecting a review");
        }

        review.setStatus(moderationDTO.getStatus());
        review.setModeratedBy(moderationDTO.getModeratedBy());
        review.setRejectionReason(
                moderationDTO.getStatus() == ReviewStatus.REJECTED ? moderationDTO.getRejectionReason() : null);

        Review moderatedReview = reviewRepository.save(review);

        // Update book rating if approved
        if (moderatedReview.isApproved()) {
            updateBookServiceRating(moderatedReview.getBookId());
        }

        log.info("Moderated review ID: {} to status: {}", reviewId, moderationDTO.getStatus());
        return mapToResponseDTO(moderatedReview);
    }

    @Override
    public ReviewResponseDTO approveReview(Long reviewId, Long moderatorId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));

        review.setStatus(ReviewStatus.APPROVED);
        review.setModeratedBy(moderatorId);
        review.setRejectionReason(null);

        Review approvedReview = reviewRepository.save(review);

        // Update book rating in Book Service
        updateBookServiceRating(approvedReview.getBookId());

        log.info("Approved review ID: {} by moderator: {}", reviewId, moderatorId);
        return mapToResponseDTO(approvedReview);
    }

    @Override
    public ReviewResponseDTO rejectReview(Long reviewId, Long moderatorId, String reason) {
        if (reason == null || reason.isBlank()) {
            throw new InvalidModerationException("Rejection reason is required");
        }

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));

        review.setStatus(ReviewStatus.REJECTED);
        review.setModeratedBy(moderatorId);
        review.setRejectionReason(reason);

        Review rejectedReview = reviewRepository.save(review);
        log.info("Rejected review ID: {} by moderator: {}. Reason: {}",
                reviewId, moderatorId, reason);

        return mapToResponseDTO(rejectedReview);
    }

    // ============================================================================
    // DELETE Operations
    // ============================================================================

    @Override
    public void deleteReview(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException(reviewId));

        if (!review.getUserId().equals(userId)) {
            throw new UnauthorizedReviewAccessException(userId, reviewId);
        }

        Long bookId = review.getBookId();
        reviewRepository.deleteById(reviewId);

        // Update book rating after deletion
        updateBookServiceRating(bookId);
    }

    @Override
    public void deleteReviewByAdmin(Long reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new ReviewNotFoundException(reviewId);
        }

        Review review = reviewRepository.findById(reviewId).orElseThrow();
        Long bookId = review.getBookId();

        reviewRepository.deleteById(reviewId);

        // Update book rating after deletion
        updateBookServiceRating(bookId);
    }

    // ============================================================================
    // Private Helper Methods
    // ============================================================================

    private ReviewResponseDTO mapToResponseDTO(Review review) {
        return ReviewResponseDTO.builder()
                .reviewId(review.getReviewId())
                .userId(review.getUserId())
                .bookId(review.getBookId())
                .rating(review.getRating())
                .comment(review.getComment())
                .status(review.getStatus())
                .moderatedBy(review.getModeratedBy())
                .rejectionReason(review.getRejectionReason())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }

    /**
     * Calculates average rating for a book (approved reviews only).
     * Private helper method for internal use.
     * 
     * @param bookId the book ID
     * @return average rating or 0.0 if no reviews
     */
    private Double calculateAverageRating(Long bookId) {
        Double average = reviewRepository.calculateAverageRating(bookId);
        return average != null ? average : 0.0;
    }

    /**
     * Updates book rating in Book Service via FeignClient.
     * This maintains data consistency across services.
     * 
     * Uses fallback if Book Service is unavailable - the local operation
     * succeeds but the rating update is logged as a warning.
     * 
     * @param bookId the book ID to update rating for
     */
    private void updateBookServiceRating(Long bookId) {
        try {
            Double averageRating = calculateAverageRating(bookId);
            Long totalReviews = reviewRepository.countByBookIdAndStatus(
                    bookId, ReviewStatus.APPROVED);

            BookRatingUpdateRequest ratingUpdate = BookRatingUpdateRequest.builder()
                    .averageRating(averageRating)
                    .totalReviews(totalReviews)
                    .build();

            bookServiceClient.updateBookRating(bookId, ratingUpdate);
            log.info("Updated book rating for book ID: {} - Average: {}, Total: {}",
                    bookId, averageRating, totalReviews);

        } catch (Exception e) {
            // Log error but don't fail the review operation
            log.error("Failed to update book rating for book ID: {}. Error: {}",
                    bookId, e.getMessage());
        }
    }
}
