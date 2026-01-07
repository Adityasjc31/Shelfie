package com.book.management.review_rating.service;

import java.util.List;

import com.book.management.review_rating.dto.ReviewCreateDTO;
import com.book.management.review_rating.dto.ReviewModerationDTO;
import com.book.management.review_rating.dto.ReviewResponseDTO;
import com.book.management.review_rating.dto.ReviewUpdateDTO;
import com.book.management.review_rating.model.Review.ReviewStatus;

/**
 * Service interface defining business logic operations for Review Management.
 * Follows the Interface Segregation Principle (ISP) from SOLID principles.
 * 
 * @author Aditya Srivastava
 * @version 1.0
 * @since 2024-12-15
 */
public interface ReviewService {

    /**
     * Creates a new review for a book.
     * 
     * @param createDTO the review creation data
     * @return the created review response
     * @throws DuplicateReviewException if user has already reviewed the book
     */
    ReviewResponseDTO createReview(ReviewCreateDTO createDTO);

    /**
     * Retrieves a review by its ID.
     * 
     * @param reviewId the review ID
     * @return the review response
     * @throws ReviewNotFoundException if review not found
     */
    ReviewResponseDTO getReviewById(Long reviewId);

    /**
     * Retrieves all reviews.
     * 
     * @return list of all reviews
     */
    List<ReviewResponseDTO> getAllReviews();

    /**
     * Retrieves all reviews for a specific book.
     * 
     * @param bookId the book ID
     * @return list of reviews for the book
     */
    List<ReviewResponseDTO> getReviewsByBookId(Long bookId);

    /**
     * Retrieves all approved reviews for a specific book.
     * 
     * @param bookId the book ID
     * @return list of approved reviews
     */
    List<ReviewResponseDTO> getApprovedReviewsByBookId(Long bookId);

    /**
     * Retrieves all reviews by a specific user.
     * 
     * @param userId the user ID
     * @return list of reviews by the user
     */
    List<ReviewResponseDTO> getReviewsByUserId(Long userId);

    /**
     * Retrieves all reviews by status.
     * 
     * @param status the review status
     * @return list of reviews with the given status
     */
    List<ReviewResponseDTO> getReviewsByStatus(ReviewStatus status);

    /**
     * Retrieves all pending reviews (for moderation).
     * 
     * @return list of pending reviews
     */
    List<ReviewResponseDTO> getPendingReviews();

    /**
     * Updates a review (only by the review owner).
     * 
     * @param reviewId  the review ID
     * @param userId    the user ID attempting the update
     * @param updateDTO the update data
     * @return the updated review response
     * @throws ReviewNotFoundException           if review not found
     * @throws UnauthorizedReviewAccessException if user is not the owner
     */
    ReviewResponseDTO updateReview(Long reviewId, Long userId, ReviewUpdateDTO updateDTO);

    /**
     * Moderates a review (approve or reject).
     * Admin-only operation.
     * 
     * @param reviewId      the review ID
     * @param moderationDTO the moderation data
     * @return the moderated review response
     * @throws ReviewNotFoundException    if review not found
     * @throws InvalidModerationException if moderation is invalid
     */
    ReviewResponseDTO moderateReview(Long reviewId, ReviewModerationDTO moderationDTO);

    /**
     * Approves a review.
     * 
     * @param reviewId    the review ID
     * @param moderatorId the moderator user ID
     * @return the approved review response
     * @throws ReviewNotFoundException if review not found
     */
    ReviewResponseDTO approveReview(Long reviewId, Long moderatorId);

    /**
     * Rejects a review.
     * 
     * @param reviewId    the review ID
     * @param moderatorId the moderator user ID
     * @param reason      the rejection reason
     * @return the rejected review response
     * @throws ReviewNotFoundException if review not found
     */
    ReviewResponseDTO rejectReview(Long reviewId, Long moderatorId, String reason);

    /**
     * Deletes a review.
     * Can be deleted by review owner or admin.
     * 
     * @param reviewId the review ID
     * @param userId   the user ID attempting deletion
     * @throws ReviewNotFoundException           if review not found
     * @throws UnauthorizedReviewAccessException if user is not authorized
     */
    void deleteReview(Long reviewId, Long userId);

    /**
     * Deletes a review (admin only - no authorization check).
     * 
     * @param reviewId the review ID
     * @throws ReviewNotFoundException if review not found
     */
    void deleteReviewByAdmin(Long reviewId);

}