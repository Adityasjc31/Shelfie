package com.book.management.review_rating.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.book.management.book.dto.responsedto.BookResponseDTO;
import com.book.management.book.service.BookService;
import com.book.management.review_rating.dto.BookRatingStatsDTO;
import com.book.management.review_rating.dto.RatingDistribution;
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
import com.book.management.review_rating.repository.impl.ReviewRepositoryImpl;
import com.book.management.review_rating.service.ReviewService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of ReviewService interface with Book Module Integration.
 * 
 * This service handles all business logic for review management operations and
 * integrates with the Book module to ensure data consistency and validation.
 * 
 * Integration Architecture:
 * - ReviewService → BookService (Dependency: Review module depends on Book module)
 * - Validates book existence before creating reviews
 * - Follows Dependency Inversion Principle (depends on abstraction, not implementation)
 * - No circular dependencies (Book module does NOT depend on Review module)
 * 
 * Design Principles Applied:
 * - Handles only review-related business logic
 * - Open for extension, closed for modification
 * - Can be replaced with any ReviewService implementation
 * - Depends only on required BookService methods
 * - Depends on BookService interface, not concrete implementation
 * 
 * @author Aditya Srivastava
 * @version 2.0 - Aditya Srivastava
 * @since 2024-12-16
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    // ============================================================================
    // Dependencies - Following Dependency Inversion Principle
    // ============================================================================
    
    /**
     * Repository for review data persistence.
     * Handles all database operations for reviews.
     */
    private final ReviewRepositoryImpl reviewRepository;
    
    /**
     * Service dependency for Book module integration.
     * 
     * Integration Pattern: Service-to-Service Communication
     * - ReviewService depends on BookService (one-way dependency)
     * - Used to validate book existence before creating reviews
     * - Ensures referential integrity at the service layer
     * - No circular dependency (BookService does NOT depend on ReviewService)
     * 
     * Why this approach?
     * - Maintains loose coupling between modules
     * - Allows independent deployment and testing of modules
     * - Follows the Dependency Rule from Clean Architecture
     * - Book module remains independent and reusable
     */
    private final BookService bookService;

    // ============================================================================
    // CREATE Operations - With Book Validation
    // ============================================================================

    /**
     * Creates a new review for a book with comprehensive validation.
     * 
     * Transaction Flow:
     * 1. Validate that the book exists (Integration Point)
     * 2. Check for duplicate reviews by the same user
     * 3. Create and persist the review
     * 4. Return the created review response
     * 
     * Integration Point: Calls BookService.getBookById() to validate book existence
     * 
     * Business Rules:
     * - Each user can only review a book once
     * - Book must exist in the system before it can be reviewed
     * - Reviews are created in PENDING status and require moderation
     * 
     * @param createDTO the review creation data containing userId, bookId, rating, and comment
     * @return ReviewResponseDTO the created review with all details
     * @throws InvalidReviewOperationException if the book does not exist
     * @throws DuplicateReviewException if user has already reviewed the book
     */
    @Override
    public ReviewResponseDTO createReview(ReviewCreateDTO createDTO) {
        log.info("Creating review for book ID: {} by user ID: {}", 
                createDTO.getBookId(), createDTO.getUserId());
        
        // ========================================================================
        // STEP 1: Book Existence Validation (Integration with Book Module)
        // ========================================================================
        
        log.debug("Validating book existence for book ID: {}", createDTO.getBookId());
        
        Optional<BookResponseDTO> bookOptional = bookService.getBookById(createDTO.getBookId());
        
        if (bookOptional.isEmpty()) {
            log.error("Cannot create review: Book not found with ID: {}", createDTO.getBookId());
            throw new InvalidReviewOperationException(
                String.format("Cannot create review: Book with ID %d does not exist", 
                    createDTO.getBookId())
            );
        }
        
        BookResponseDTO book = bookOptional.get();
        log.info("Book validation successful. Book title: '{}', Author ID: {}", 
                book.getBookTitle(), book.getBookAuthorId());
        
        // ========================================================================
        // STEP 2: Duplicate Review Check
        // ========================================================================
        
        log.debug("Checking for duplicate reviews by user ID: {} for book ID: {}", 
                createDTO.getUserId(), createDTO.getBookId());
        
        if (reviewRepository.existsByUserIdAndBookId(createDTO.getUserId(), createDTO.getBookId())) {
            log.error("Duplicate review detected: User {} has already reviewed book {}", 
                    createDTO.getUserId(), createDTO.getBookId());
            throw new DuplicateReviewException(createDTO.getUserId(), createDTO.getBookId());
        }
        
        // ========================================================================
        // STEP 3: Create Review Entity
        // ========================================================================

        log.debug("Creating new review entity");
        
        Review review = Review.builder()
                .userId(createDTO.getUserId())
                .bookId(createDTO.getBookId())
                .rating(createDTO.getRating())
                .comment(createDTO.getComment())
                .status(ReviewStatus.PENDING) // All reviews start as PENDING for moderation
                .helpfulCount(0)
                .build();

        // ========================================================================
        // STEP 4: Persist Review
        // ========================================================================
        
        Review savedReview = reviewRepository.save(review);
        log.info("Successfully created review with ID: {} for book: '{}'", 
                savedReview.getReviewId(), book.getBookTitle());
        
        return mapToResponseDTO(savedReview);
    }

    // ============================================================================
    // READ Operations
    // ============================================================================

    /**
     * Retrieves a review by its unique identifier.
     * 
     * @param reviewId the unique identifier of the review
     * @return ReviewResponseDTO containing all review details
     * @throws ReviewNotFoundException if review with given ID does not exist
     */
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

    /**
     * Retrieves all reviews in the system.
     * 
     * Use Case: Admin dashboard to view all reviews for moderation
     * 
     * @return List of all reviews
     */
    @Override
    public List<ReviewResponseDTO> getAllReviews() {
        log.info("Fetching all reviews");
        
        List<Review> reviews = reviewRepository.findAll();
        log.info("Successfully fetched {} reviews", reviews.size());
        
        return reviews.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all reviews for a specific book.
     * 
     * Use Case: Display all reviews (including pending/rejected) for admin view
     * 
     * Note: This returns ALL reviews regardless of status. For customer-facing
     * reviews, use getApprovedReviewsByBookId() instead.
     * 
     * @param bookId the unique identifier of the book
     * @return List of all reviews for the specified book
     */
    @Override
    public List<ReviewResponseDTO> getReviewsByBookId(Long bookId) {
        log.info("Fetching all reviews for book ID: {}", bookId);
        
        List<Review> reviews = reviewRepository.findByBookId(bookId);
        log.info("Successfully fetched {} reviews for book ID: {}", reviews.size(), bookId);
        
        return reviews.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves only approved reviews for a specific book.
     * 
     * Use Case: Display customer-facing reviews on book detail pages
     * 
     * Business Rule: Only approved reviews are visible to customers
     * 
     * @param bookId the unique identifier of the book
     * @return List of approved reviews for the specified book
     */
    @Override
    public List<ReviewResponseDTO> getApprovedReviewsByBookId(Long bookId) {
        log.info("Fetching approved reviews for book ID: {}", bookId);
        
        List<Review> reviews = reviewRepository.findApprovedReviewsByBookId(bookId);
        log.info("Successfully fetched {} approved reviews for book ID: {}", reviews.size(), bookId);
        
        return reviews.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all reviews created by a specific user.
     * 
     * Use Case: User profile page showing their review history
     * 
     * @param userId the unique identifier of the user
     * @return List of reviews created by the user
     */
    @Override
    public List<ReviewResponseDTO> getReviewsByUserId(Long userId) {
        log.info("Fetching reviews by user ID: {}", userId);
        
        List<Review> reviews = reviewRepository.findByUserId(userId);
        log.info("Successfully fetched {} reviews by user ID: {}", reviews.size(), userId);
        
        return reviews.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all reviews with a specific status.
     * 
     * Use Case: Admin moderation dashboard filtering by status
     * 
     * @param status the review status (PENDING, APPROVED, REJECTED)
     * @return List of reviews with the specified status
     */
    @Override
    public List<ReviewResponseDTO> getReviewsByStatus(ReviewStatus status) {
        log.info("Fetching reviews by status: {}", status);
        
        List<Review> reviews = reviewRepository.findByStatus(status);
        log.info("Successfully fetched {} reviews with status: {}", reviews.size(), status);
        
        return reviews.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all pending reviews awaiting moderation.
     * 
     * Use Case: Admin moderation queue
     * 
     * Business Rule: New reviews are created in PENDING status and must be
     * approved or rejected by a moderator before becoming visible to customers.
     * 
     * @return List of pending reviews
     */
    @Override
    public List<ReviewResponseDTO> getPendingReviews() {
        log.info("Fetching pending reviews for moderation");
        
        List<Review> reviews = reviewRepository.findPendingReviews();
        log.info("Successfully fetched {} pending reviews", reviews.size());
        
        return reviews.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all reviews with a specific rating.
     * 
     * Use Case: Analytics and filtering reviews by rating
     * 
     * @param rating the rating value (1-5)
     * @return List of reviews with the specified rating
     * @throws InvalidReviewOperationException if rating is not between 1 and 5
     */
    @Override
    public List<ReviewResponseDTO> getReviewsByRating(Integer rating) {
        log.info("Fetching reviews with rating: {}", rating);
        
        if (rating < 1 || rating > 5) {
            log.error("Invalid rating value: {}. Must be between 1 and 5", rating);
            throw new InvalidReviewOperationException("Rating must be between 1 and 5");
        }
        
        List<Review> reviews = reviewRepository.findByRating(rating);
        log.info("Successfully fetched {} reviews with rating: {}", reviews.size(), rating);
        
        return reviews.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    // ============================================================================
    // UPDATE Operations
    // ============================================================================

    /**
     * Updates an existing review.
     * 
     * Business Rules:
     * - Only the review owner can update their review
     * - Rejected reviews cannot be updated
     * - Updated reviews are reset to PENDING status for re-moderation
     * 
     * Security: Enforces user authorization to prevent unauthorized modifications
     * 
     * @param reviewId the unique identifier of the review to update
     * @param userId the ID of the user attempting the update
     * @param updateDTO the new review data (rating and comment)
     * @return ReviewResponseDTO with updated review data
     * @throws ReviewNotFoundException if review does not exist
     * @throws UnauthorizedReviewAccessException if userId does not match review owner
     * @throws InvalidReviewOperationException if review is rejected
     */
    @Override
    public ReviewResponseDTO updateReview(Long reviewId, Long userId, ReviewUpdateDTO updateDTO) {
        log.info("Updating review ID: {} by user ID: {}", reviewId, userId);
        
        // Fetch existing review
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> {
                    log.error("Review not found with ID: {}", reviewId);
                    return new ReviewNotFoundException(reviewId);
                });

        // Authorization check: Only owner can update
        if (!review.getUserId().equals(userId)) {
            log.error("Authorization failed: User {} attempted to update review {} owned by user {}", 
                    userId, reviewId, review.getUserId());
            throw new UnauthorizedReviewAccessException(userId, reviewId);
        }

        // Business rule: Cannot update rejected reviews
        if (review.isRejected()) {
            log.error("Cannot update rejected review: {}", reviewId);
            throw new InvalidReviewOperationException(
                "Cannot update rejected review. Please create a new review instead."
            );
        }

        // Update review data
        review.setRating(updateDTO.getRating());
        review.setComment(updateDTO.getComment());
        review.setStatus(ReviewStatus.PENDING); // Reset to pending for re-moderation
        
        Review updatedReview = reviewRepository.save(review);
        log.info("Successfully updated review ID: {}. Status reset to PENDING for re-moderation", reviewId);
        
        return mapToResponseDTO(updatedReview);
    }

    /**
     * Moderates a review by changing its status.
     * 
     * Admin Operation: Only moderators/admins should call this method
     * 
     * Business Rules:
     * - Rejected reviews must include a rejection reason
     * - Moderation sets the moderatedBy field for audit trail
     * 
     * @param reviewId the unique identifier of the review
     * @param moderationDTO contains status, moderator ID, and optional rejection reason
     * @return ReviewResponseDTO with updated moderation status
     * @throws ReviewNotFoundException if review does not exist
     * @throws InvalidModerationException if rejection reason is missing for REJECTED status
     */
    @Override
    public ReviewResponseDTO moderateReview(Long reviewId, ReviewModerationDTO moderationDTO) {
        log.info("Moderating review ID: {} to status: {} by moderator ID: {}", 
                reviewId, moderationDTO.getStatus(), moderationDTO.getModeratedBy());
        
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> {
                    log.error("Review not found with ID: {}", reviewId);
                    return new ReviewNotFoundException(reviewId);
                });

        // Validation: Rejection requires a reason
        if (moderationDTO.getStatus() == ReviewStatus.REJECTED && 
            (moderationDTO.getRejectionReason() == null || moderationDTO.getRejectionReason().isBlank())) {
            log.error("Rejection reason missing for review ID: {}", reviewId);
            throw new InvalidModerationException(
                "Rejection reason is required when rejecting a review"
            );
        }

        // Update review moderation status
        review.setStatus(moderationDTO.getStatus());
        review.setModeratedBy(moderationDTO.getModeratedBy());
        
        if (moderationDTO.getStatus() == ReviewStatus.REJECTED) {
            review.setRejectionReason(moderationDTO.getRejectionReason());
            log.info("Review {} rejected with reason: {}", reviewId, moderationDTO.getRejectionReason());
        } else {
            review.setRejectionReason(null); // Clear rejection reason if not rejected
        }
        
        Review moderatedReview = reviewRepository.save(review);
        log.info("Successfully moderated review ID: {} to status: {}", reviewId, moderationDTO.getStatus());
        
        return mapToResponseDTO(moderatedReview);
    }

    /**
     * Approves a review.
     * 
     * Convenience method for approving reviews without full moderation DTO.
     * 
     * @param reviewId the unique identifier of the review
     * @param moderatorId the ID of the moderator approving the review
     * @return ReviewResponseDTO with approved status
     * @throws ReviewNotFoundException if review does not exist
     */
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

    /**
     * Rejects a review with a reason.
     * 
     * Convenience method for rejecting reviews.
     * 
     * @param reviewId the unique identifier of the review
     * @param moderatorId the ID of the moderator rejecting the review
     * @param reason the reason for rejection (required)
     * @return ReviewResponseDTO with rejected status
     * @throws ReviewNotFoundException if review does not exist
     * @throws InvalidModerationException if reason is blank
     */
    @Override
    public ReviewResponseDTO rejectReview(Long reviewId, Long moderatorId, String reason) {
        log.info("Rejecting review ID: {} by moderator ID: {}", reviewId, moderatorId);
        
        if (reason == null || reason.isBlank()) {
            log.error("Rejection reason is required but was not provided");
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
        log.info("Successfully rejected review ID: {} with reason: {}", reviewId, reason);
        
        return mapToResponseDTO(rejectedReview);
    }

    /**
     * Increments the helpful count for a review.
     * 
     * Use Case: Users can mark reviews as helpful, similar to "thumbs up" feature
     * 
     * Note: In a production system, you might want to track which users marked
     * a review as helpful to prevent multiple votes from the same user.
     * 
     * @param reviewId the unique identifier of the review
     * @return ReviewResponseDTO with incremented helpful count
     * @throws ReviewNotFoundException if review does not exist
     */
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
        
        log.info("Successfully marked review ID: {} as helpful. New count: {}", 
                reviewId, updatedReview.getHelpfulCount());
        
        return mapToResponseDTO(updatedReview);
    }

    // ============================================================================
    // ANALYTICS Operations
    // ============================================================================

    /**
     * Calculates comprehensive rating statistics for a book.
     * 
     * Use Case: Display detailed rating breakdown on book detail pages
     * 
     * Statistics Include:
     * - Average rating (based on approved reviews only)
     * - Total review counts by status
     * - Rating distribution (5-star, 4-star, etc.)
     * 
     * @param bookId the unique identifier of the book
     * @return BookRatingStatsDTO containing all rating statistics
     */
    @Override
    public BookRatingStatsDTO getBookRatingStats(Long bookId) {
        log.info("Calculating comprehensive rating statistics for book ID: {}", bookId);
        
        List<Review> allReviews = reviewRepository.findByBookId(bookId);
        List<Review> approvedReviews = reviewRepository.findApprovedReviewsByBookId(bookId);
        
        // Calculate average rating (approved reviews only)
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
        
        // Count reviews by status
        long pendingCount = allReviews.stream().filter(Review::isPending).count();
        long rejectedCount = allReviews.stream().filter(Review::isRejected).count();
        
        BookRatingStatsDTO stats = BookRatingStatsDTO.builder()
                .bookId(bookId)
                .averageRating(averageRating)
                .totalReviews((long) allReviews.size())
                .approvedReviews((long) approvedReviews.size())
                .pendingReviews(pendingCount)
                .rejectedReviews(rejectedCount)
                .ratingDistribution(ratingDist)
                .build();
        
        log.info("Successfully calculated stats for book ID: {}. Avg rating: {}, Total reviews: {}", 
                bookId, averageRating, allReviews.size());
        
        return stats;
    }

    /**
     * Calculates the average rating for a book.
     * 
     * Convenience method for getting just the average rating.
     * 
     * Business Rule: Only approved reviews are included in the calculation
     * 
     * @param bookId the unique identifier of the book
     * @return Double average rating, or 0.0 if no approved reviews exist
     */
    @Override
    public Double calculateAverageRating(Long bookId) {
        log.info("Calculating average rating for book ID: {}", bookId);
        
        Double average = reviewRepository.calculateAverageRating(bookId);
        log.info("Average rating for book ID {}: {}", bookId, average);
        
        return average;
    }

    // ============================================================================
    // DELETE Operations
    // ============================================================================

    /**
     * Deletes a review by its owner.
     * 
     * Business Rule: Only the review owner can delete their own review
     * 
     * Security: Enforces user authorization
     * 
     * @param reviewId the unique identifier of the review
     * @param userId the ID of the user attempting deletion
     * @throws ReviewNotFoundException if review does not exist
     * @throws UnauthorizedReviewAccessException if userId does not match review owner
     */
    @Override
    public void deleteReview(Long reviewId, Long userId) {
        log.info("Deleting review ID: {} by user ID: {}", reviewId, userId);
        
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> {
                    log.error("Review not found with ID: {}", reviewId);
                    return new ReviewNotFoundException(reviewId);
                });

        // Authorization check: Only owner can delete
        if (!review.getUserId().equals(userId)) {
            log.error("Authorization failed: User {} attempted to delete review {} owned by user {}", 
                    userId, reviewId, review.getUserId());
            throw new UnauthorizedReviewAccessException(userId, reviewId);
        }

        reviewRepository.deleteById(reviewId);
        log.info("Successfully deleted review ID: {}", reviewId);
    }

    /**
     * Deletes a review (admin operation).
     * 
     * Admin Operation: No authorization check performed
     * Use Case: Moderators removing inappropriate content
     * 
     * @param reviewId the unique identifier of the review
     * @throws ReviewNotFoundException if review does not exist
     */
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

    // ============================================================================
    // Private Helper Methods - Mapping
    // ============================================================================

    /**
     * Maps Review entity to ReviewResponseDTO.
     * 
     * Encapsulates the mapping logic for maintainability.
     * If mapping logic becomes complex, consider using MapStruct or ModelMapper.
     * 
     * @param review the review entity to map
     * @return ReviewResponseDTO the mapped response DTO
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