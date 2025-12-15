package com.db.ms.review_rating_temp.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.db.ms.review_rating_temp.dto.BookRatingStatsDTO;
import com.db.ms.review_rating_temp.dto.RatingDistribution;
import com.db.ms.review_rating_temp.dto.ReviewCreateDTO;
import com.db.ms.review_rating_temp.dto.ReviewModerationDTO;
import com.db.ms.review_rating_temp.dto.ReviewResponseDTO;
import com.db.ms.review_rating_temp.dto.ReviewUpdateDTO;
import com.db.ms.review_rating_temp.exception.DuplicateReviewException;
import com.db.ms.review_rating_temp.exception.InvalidModerationException;
import com.db.ms.review_rating_temp.exception.InvalidReviewOperationException;
import com.db.ms.review_rating_temp.exception.ReviewNotFoundException;
import com.db.ms.review_rating_temp.exception.UnauthorizedReviewAccessException;
import com.db.ms.review_rating_temp.model.Review;
import com.db.ms.review_rating_temp.model.Review.ReviewStatus;
import com.db.ms.review_rating_temp.repository.ReviewRepository;
import com.db.ms.review_rating_temp.service.ReviewService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of ReviewService interface.
 * Handles all business logic for review management operations.
 * Follows Single Responsibility and Dependency Inversion Principles.
 * 
 * @author Bookstore Development Team
 * @version 1.0
 * @since 2024-12-15
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;

    @Override
    public ReviewResponseDTO createReview(ReviewCreateDTO createDTO) {
        log.info("Creating review for book ID: {} by user ID: {}", 
                createDTO.getBookId(), createDTO.getUserId());
        
        // Check for duplicate review
        if (reviewRepository.existsByUserIdAndBookId(createDTO.getUserId(), createDTO.getBookId())) {
            log.error("User {} has already reviewed book {}", 
                    createDTO.getUserId(), createDTO.getBookId());
            throw new DuplicateReviewException(createDTO.getUserId(), createDTO.getBookId());
        }

        Review review = Review.builder()
                .userId(createDTO.getUserId())
                .bookId(createDTO.getBookId())
                .rating(createDTO.getRating())
                .comment(createDTO.getComment())
                .status(ReviewStatus.PENDING)
                .helpfulCount(0)
                .build();

        Review savedReview = reviewRepository.save(review);
        log.info("Successfully created review with ID: {}", savedReview.getReviewId());
        
        return mapToResponseDTO(savedReview);
    }

    @Override
    public ReviewResponseDTO getReviewById(Long reviewId) {
        log.info("Fetching review by ID: {}", reviewId);
        
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> {
                    log.error("Review not found with ID: {}", reviewId);
                    return new ReviewNotFoundException(reviewId);
                });
        
        log.info("Successfully fetched review with ID: {}", reviewId);
        return mapToResponseDTO(review);
    }

    @Override
    public List<ReviewResponseDTO> getAllReviews() {
        log.info("Fetching all reviews");
        
        List<Review> reviews = reviewRepository.findAll();
        log.info("Successfully fetched {} reviews", reviews.size());
        
        return reviews.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewResponseDTO> getReviewsByBookId(Long bookId) {
        log.info("Fetching reviews for book ID: {}", bookId);
        
        List<Review> reviews = reviewRepository.findByBookId(bookId);
        log.info("Successfully fetched {} reviews for book ID: {}", reviews.size(), bookId);
        
        return reviews.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewResponseDTO> getApprovedReviewsByBookId(Long bookId) {
        log.info("Fetching approved reviews for book ID: {}", bookId);
        
        List<Review> reviews = reviewRepository.findApprovedReviewsByBookId(bookId);
        log.info("Successfully fetched {} approved reviews for book ID: {}", reviews.size(), bookId);
        
        return reviews.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewResponseDTO> getReviewsByUserId(Long userId) {
        log.info("Fetching reviews by user ID: {}", userId);
        
        List<Review> reviews = reviewRepository.findByUserId(userId);
        log.info("Successfully fetched {} reviews by user ID: {}", reviews.size(), userId);
        
        return reviews.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewResponseDTO> getReviewsByStatus(ReviewStatus status) {
        log.info("Fetching reviews by status: {}", status);
        
        List<Review> reviews = reviewRepository.findByStatus(status);
        log.info("Successfully fetched {} reviews with status: {}", reviews.size(), status);
        
        return reviews.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewResponseDTO> getPendingReviews() {
        log.info("Fetching pending reviews for moderation");
        
        List<Review> reviews = reviewRepository.findPendingReviews();
        log.info("Successfully fetched {} pending reviews", reviews.size());
        
        return reviews.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewResponseDTO> getReviewsByRating(Integer rating) {
        log.info("Fetching reviews with rating: {}", rating);
        
        if (rating < 1 || rating > 5) {
            throw new InvalidReviewOperationException("Rating must be between 1 and 5");
        }
        
        List<Review> reviews = reviewRepository.findByRating(rating);
        log.info("Successfully fetched {} reviews with rating: {}", reviews.size(), rating);
        
        return reviews.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ReviewResponseDTO updateReview(Long reviewId, Long userId, ReviewUpdateDTO updateDTO) {
        log.info("Updating review ID: {} by user ID: {}", reviewId, userId);
        
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> {
                    log.error("Review not found with ID: {}", reviewId);
                    return new ReviewNotFoundException(reviewId);
                });

        // Check authorization
        if (!review.getUserId().equals(userId)) {
            log.error("User {} is not authorized to update review {}", userId, reviewId);
            throw new UnauthorizedReviewAccessException(userId, reviewId);
        }

        // Only pending or approved reviews can be updated
        if (review.isRejected()) {
            log.error("Cannot update rejected review {}", reviewId);
            throw new InvalidReviewOperationException("Cannot update rejected review");
        }

        review.setRating(updateDTO.getRating());
        review.setComment(updateDTO.getComment());
        review.setStatus(ReviewStatus.PENDING); // Reset to pending after update
        
        Review updatedReview = reviewRepository.save(review);
        log.info("Successfully updated review ID: {}", reviewId);
        
        return mapToResponseDTO(updatedReview);
    }

    @Override
    public ReviewResponseDTO moderateReview(Long reviewId, ReviewModerationDTO moderationDTO) {
        log.info("Moderating review ID: {} with status: {}", reviewId, moderationDTO.getStatus());
        
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> {
                    log.error("Review not found with ID: {}", reviewId);
                    return new ReviewNotFoundException(reviewId);
                });

        // Validate moderation
        if (moderationDTO.getStatus() == ReviewStatus.REJECTED && 
            (moderationDTO.getRejectionReason() == null || moderationDTO.getRejectionReason().isBlank())) {
            log.error("Rejection reason is required for rejecting a review");
            throw new InvalidModerationException("Rejection reason is required");
        }

        review.setStatus(moderationDTO.getStatus());
        review.setModeratedBy(moderationDTO.getModeratedBy());
        
        if (moderationDTO.getStatus() == ReviewStatus.REJECTED) {
            review.setRejectionReason(moderationDTO.getRejectionReason());
        }
        
        Review moderatedReview = reviewRepository.save(review);
        log.info("Successfully moderated review ID: {} to status: {}", reviewId, moderationDTO.getStatus());
        
        return mapToResponseDTO(moderatedReview);
    }

    @Override
    public ReviewResponseDTO approveReview(Long reviewId, Long moderatorId) {
        log.info("Approving review ID: {} by moderator ID: {}", reviewId, moderatorId);
        
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> {
                    log.error("Review not found with ID: {}", reviewId);
                    return new ReviewNotFoundException(reviewId);
                });

        review.setStatus(ReviewStatus.APPROVED);
        review.setModeratedBy(moderatorId);
        review.setRejectionReason(null);
        
        Review approvedReview = reviewRepository.save(review);
        log.info("Successfully approved review ID: {}", reviewId);
        
        return mapToResponseDTO(approvedReview);
    }

    @Override
    public ReviewResponseDTO rejectReview(Long reviewId, Long moderatorId, String reason) {
        log.info("Rejecting review ID: {} by moderator ID: {}", reviewId, moderatorId);
        
        if (reason == null || reason.isBlank()) {
            throw new InvalidModerationException("Rejection reason is required");
        }
        
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> {
                    log.error("Review not found with ID: {}", reviewId);
                    return new ReviewNotFoundException(reviewId);
                });

        review.setStatus(ReviewStatus.REJECTED);
        review.setModeratedBy(moderatorId);
        review.setRejectionReason(reason);
        
        Review rejectedReview = reviewRepository.save(review);
        log.info("Successfully rejected review ID: {}", reviewId);
        
        return mapToResponseDTO(rejectedReview);
    }

    @Override
    public ReviewResponseDTO markReviewAsHelpful(Long reviewId) {
        log.info("Marking review ID: {} as helpful", reviewId);
        
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> {
                    log.error("Review not found with ID: {}", reviewId);
                    return new ReviewNotFoundException(reviewId);
                });

        review.setHelpfulCount(review.getHelpfulCount() + 1);
        Review updatedReview = reviewRepository.save(review);
        
        log.info("Successfully marked review ID: {} as helpful. Count: {}", 
                reviewId, updatedReview.getHelpfulCount());
        
        return mapToResponseDTO(updatedReview);
    }

    @Override
    public BookRatingStatsDTO getBookRatingStats(Long bookId) {
        log.info("Calculating rating statistics for book ID: {}", bookId);
        
        List<Review> allReviews = reviewRepository.findByBookId(bookId);
        List<Review> approvedReviews = reviewRepository.findApprovedReviewsByBookId(bookId);
        
        Double averageRating = reviewRepository.calculateAverageRating(bookId);
        
        // Calculate rating distribution
        Map<Integer, Long> distribution = approvedReviews.stream()
                .collect(Collectors.groupingBy(Review::getRating, Collectors.counting()));
        
        RatingDistribution ratingDist = RatingDistribution.builder()
                .fiveStars(distribution.getOrDefault(5, 0L))
                .fourStars(distribution.getOrDefault(4, 0L))
                .threeStars(distribution.getOrDefault(3, 0L))
                .twoStars(distribution.getOrDefault(2, 0L))
                .oneStar(distribution.getOrDefault(1, 0L))
                .build();
        
        long pendingCount = allReviews.stream()
                .filter(Review::isPending)
                .count();
        
        long rejectedCount = allReviews.stream()
                .filter(Review::isRejected)
                .count();
        
        BookRatingStatsDTO stats = BookRatingStatsDTO.builder()
                .bookId(bookId)
                .averageRating(averageRating)
                .totalReviews((long) allReviews.size())
                .approvedReviews((long) approvedReviews.size())
                .pendingReviews(pendingCount)
                .rejectedReviews(rejectedCount)
                .ratingDistribution(ratingDist)
                .build();
        
        log.info("Successfully calculated stats for book ID: {}. Avg rating: {}", 
                bookId, averageRating);
        
        return stats;
    }

    @Override
    public Double calculateAverageRating(Long bookId) {
        log.info("Calculating average rating for book ID: {}", bookId);
        
        Double average = reviewRepository.calculateAverageRating(bookId);
        log.info("Average rating for book ID {}: {}", bookId, average);
        
        return average;
    }

    @Override
    public void deleteReview(Long reviewId, Long userId) {
        log.info("Deleting review ID: {} by user ID: {}", reviewId, userId);
        
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> {
                    log.error("Review not found with ID: {}", reviewId);
                    return new ReviewNotFoundException(reviewId);
                });

        // Check authorization
        if (!review.getUserId().equals(userId)) {
            log.error("User {} is not authorized to delete review {}", userId, reviewId);
            throw new UnauthorizedReviewAccessException(userId, reviewId);
        }

        reviewRepository.deleteById(reviewId);
        log.info("Successfully deleted review ID: {}", reviewId);
    }

    @Override
    public void deleteReviewByAdmin(Long reviewId) {
        log.info("Admin deleting review ID: {}", reviewId);
        
        if (!reviewRepository.existsById(reviewId)) {
            log.error("Review not found with ID: {}", reviewId);
            throw new ReviewNotFoundException(reviewId);
        }

        reviewRepository.deleteById(reviewId);
        log.info("Successfully deleted review ID: {} by admin", reviewId);
    }

    /**
     * Maps Review entity to ReviewResponseDTO.
     * 
     * @param review the review entity
     * @return the review response DTO
     */
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
                .helpfulCount(review.getHelpfulCount())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }
}