package com.book.management.review_rating.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.book.management.review_rating.dto.*;
import com.book.management.review_rating.model.Review.ReviewStatus;
import com.book.management.review_rating.service.ReviewService;

import jakarta.validation.Valid;
import java.util.List;

/**
 * REST Controller for Review Management operations.
 * Exposes endpoints for managing book reviews, ratings, and moderation.
 * Logging is handled automatically by LoggingAspect.
 * 
 * @author Aditya Srivastava
 * @version 1.0
 * @since 2024-12-15
 */
@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * Creates a new review.
     * 
     * @param createDTO the review creation data
     * @return ResponseEntity with created review and HTTP 201 status
     */
    @PostMapping
    public ResponseEntity<ReviewResponseDTO> createReview(
            @Valid @RequestBody ReviewCreateDTO createDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewService.createReview(createDTO));
    }

    /**
     * Retrieves a review by its ID.
     * 
     * @param reviewId the review ID
     * @return ResponseEntity with review data and HTTP 200 status
     */
    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewResponseDTO> getReviewById(@PathVariable Long reviewId) {
        return ResponseEntity.ok(reviewService.getReviewById(reviewId));
    }

    /**
     * Retrieves all reviews.
     * 
     * @return ResponseEntity with list of all reviews and HTTP 200 status
     */
    @GetMapping
    public ResponseEntity<List<ReviewResponseDTO>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }

    /**
     * Retrieves all reviews for a specific book.
     * 
     * @param bookId the book ID
     * @return ResponseEntity with list of reviews and HTTP 200 status
     */
    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<ReviewResponseDTO>> getReviewsByBookId(@PathVariable Long bookId) {
        return ResponseEntity.ok(reviewService.getReviewsByBookId(bookId));
    }

    /**
     * Retrieves all approved reviews for a specific book.
     * 
     * @param bookId the book ID
     * @return ResponseEntity with list of approved reviews and HTTP 200 status
     */
    @GetMapping("/book/{bookId}/approved")
    public ResponseEntity<List<ReviewResponseDTO>> getApprovedReviewsByBookId(
            @PathVariable Long bookId) {
        return ResponseEntity.ok(reviewService.getApprovedReviewsByBookId(bookId));
    }

    /**
     * Retrieves all reviews by a specific user.
     * 
     * @param userId the user ID
     * @return ResponseEntity with list of reviews and HTTP 200 status
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewResponseDTO>> getReviewsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(reviewService.getReviewsByUserId(userId));
    }

    /**
     * Retrieves all reviews by status.
     * 
     * @param status the review status
     * @return ResponseEntity with list of reviews and HTTP 200 status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<ReviewResponseDTO>> getReviewsByStatus(
            @PathVariable ReviewStatus status) {
        return ResponseEntity.ok(reviewService.getReviewsByStatus(status));
    }

    /**
     * Retrieves all pending reviews (for moderation).
     * 
     * @return ResponseEntity with list of pending reviews and HTTP 200 status
     */
    @GetMapping("/pending")
    public ResponseEntity<List<ReviewResponseDTO>> getPendingReviews() {
        return ResponseEntity.ok(reviewService.getPendingReviews());
    }

    /**
     * Updates a review.
     * 
     * @param reviewId  the review ID
     * @param userId    the user ID attempting the update
     * @param updateDTO the update data
     * @return ResponseEntity with updated review and HTTP 200 status
     */
    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResponseDTO> updateReview(
            @PathVariable Long reviewId,
            @RequestParam Long userId,
            @Valid @RequestBody ReviewUpdateDTO updateDTO) {
        return ResponseEntity.ok(reviewService.updateReview(reviewId, userId, updateDTO));
    }

    /**
     * Moderates a review (approve or reject).
     * 
     * @param reviewId      the review ID
     * @param moderationDTO the moderation data
     * @return ResponseEntity with moderated review and HTTP 200 status
     */
    @PatchMapping("/{reviewId}/moderate")
    public ResponseEntity<ReviewResponseDTO> moderateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewModerationDTO moderationDTO) {
        return ResponseEntity.ok(reviewService.moderateReview(reviewId, moderationDTO));
    }

    /**
     * Approves a review.
     * 
     * @param reviewId    the review ID
     * @param moderatorId the moderator user ID
     * @return ResponseEntity with approved review and HTTP 200 status
     */
    @PatchMapping("/{reviewId}/approve")
    public ResponseEntity<ReviewResponseDTO> approveReview(
            @PathVariable Long reviewId,
            @RequestParam Long moderatorId) {
        return ResponseEntity.ok(reviewService.approveReview(reviewId, moderatorId));
    }

    /**
     * Rejects a review.
     * 
     * @param reviewId    the review ID
     * @param moderatorId the moderator user ID
     * @param reason      the rejection reason
     * @return ResponseEntity with rejected review and HTTP 200 status
     */
    @PatchMapping("/{reviewId}/reject")
    public ResponseEntity<ReviewResponseDTO> rejectReview(
            @PathVariable Long reviewId,
            @RequestParam Long moderatorId,
            @RequestParam String reason) {
        return ResponseEntity.ok(reviewService.rejectReview(reviewId, moderatorId, reason));
    }

    /**
     * Deletes a review.
     * 
     * @param reviewId the review ID
     * @param userId   the user ID attempting deletion
     * @return ResponseEntity with HTTP 204 No Content status
     */
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long reviewId,
            @RequestParam Long userId) {
        reviewService.deleteReview(reviewId, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Deletes a review (admin only).
     * 
     * @param reviewId the review ID
     * @return ResponseEntity with HTTP 204 No Content status
     */
    @DeleteMapping("/{reviewId}/admin")
    public ResponseEntity<Void> deleteReviewByAdmin(@PathVariable Long reviewId) {
        reviewService.deleteReviewByAdmin(reviewId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Deletes all reviews by a specific user.
     * Used for cascading delete when a user is deleted.
     * 
     * @param userId the user ID whose reviews should be deleted
     * @return ResponseEntity with HTTP 204 No Content status
     */
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> deleteReviewsByUserId(@PathVariable Long userId) {
        reviewService.deleteReviewsByUserId(userId);
        return ResponseEntity.noContent().build();
    }
}