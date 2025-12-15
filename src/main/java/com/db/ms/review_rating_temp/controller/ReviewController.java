package com.db.ms.review_rating_temp.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.db.ms.review_rating_temp.dto.*;
import com.db.ms.review_rating_temp.model.Review.ReviewStatus;
import com.db.ms.review_rating_temp.service.ReviewService;

import jakarta.validation.Valid;
import java.util.List;

/**
 * REST Controller for Review Management operations.
 * Exposes endpoints for managing book reviews, ratings, and moderation.
 * 
 * @author Bookstore Development Team
 * @version 1.0
 * @since 2024-12-15
 */
@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Slf4j
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
        log.info("POST /api/v1/reviews - Creating review for book ID: {} by user ID: {}", 
                createDTO.getBookId(), createDTO.getUserId());
        
        ReviewResponseDTO response = reviewService.createReview(createDTO);
        
        log.info("Successfully created review with ID: {}", response.getReviewId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieves a review by its ID.
     * 
     * @param reviewId the review ID
     * @return ResponseEntity with review data and HTTP 200 status
     */
    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewResponseDTO> getReviewById(@PathVariable Long reviewId) {
        log.info("GET /api/v1/reviews/{} - Fetching review", reviewId);
        
        ReviewResponseDTO response = reviewService.getReviewById(reviewId);
        
        log.info("Successfully retrieved review with ID: {}", reviewId);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves all reviews.
     * 
     * @return ResponseEntity with list of all reviews and HTTP 200 status
     */
    @GetMapping
    public ResponseEntity<List<ReviewResponseDTO>> getAllReviews() {
        log.info("GET /api/v1/reviews - Fetching all reviews");
        
        List<ReviewResponseDTO> response = reviewService.getAllReviews();
        
        log.info("Successfully retrieved {} reviews", response.size());
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves all reviews for a specific book.
     * 
     * @param bookId the book ID
     * @return ResponseEntity with list of reviews and HTTP 200 status
     */
    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<ReviewResponseDTO>> getReviewsByBookId(@PathVariable Long bookId) {
        log.info("GET /api/v1/reviews/book/{} - Fetching reviews for book", bookId);
        
        List<ReviewResponseDTO> response = reviewService.getReviewsByBookId(bookId);
        
        log.info("Successfully retrieved {} reviews for book ID: {}", response.size(), bookId);
        return ResponseEntity.ok(response);
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
        log.info("GET /api/v1/reviews/book/{}/approved - Fetching approved reviews", bookId);
        
        List<ReviewResponseDTO> response = reviewService.getApprovedReviewsByBookId(bookId);
        
        log.info("Successfully retrieved {} approved reviews for book ID: {}", 
                response.size(), bookId);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves all reviews by a specific user.
     * 
     * @param userId the user ID
     * @return ResponseEntity with list of reviews and HTTP 200 status
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewResponseDTO>> getReviewsByUserId(@PathVariable Long userId) {
        log.info("GET /api/v1/reviews/user/{} - Fetching reviews by user", userId);
        
        List<ReviewResponseDTO> response = reviewService.getReviewsByUserId(userId);
        
        log.info("Successfully retrieved {} reviews by user ID: {}", response.size(), userId);
        return ResponseEntity.ok(response);
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
        log.info("GET /api/v1/reviews/status/{} - Fetching reviews by status", status);
        
        List<ReviewResponseDTO> response = reviewService.getReviewsByStatus(status);
        
        log.info("Successfully retrieved {} reviews with status: {}", response.size(), status);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves all pending reviews (for moderation).
     * 
     * @return ResponseEntity with list of pending reviews and HTTP 200 status
     */
    @GetMapping("/pending")
    public ResponseEntity<List<ReviewResponseDTO>> getPendingReviews() {
        log.info("GET /api/v1/reviews/pending - Fetching pending reviews");
        
        List<ReviewResponseDTO> response = reviewService.getPendingReviews();
        
        log.info("Successfully retrieved {} pending reviews", response.size());
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieves all reviews with a specific rating.
     * 
     * @param rating the rating (1-5)
     * @return ResponseEntity with list of reviews and HTTP 200 status
     */
    @GetMapping("/rating/{rating}")
    public ResponseEntity<List<ReviewResponseDTO>> getReviewsByRating(@PathVariable Integer rating) {
        log.info("GET /api/v1/reviews/rating/{} - Fetching reviews by rating", rating);
        
        List<ReviewResponseDTO> response = reviewService.getReviewsByRating(rating);
        
        log.info("Successfully retrieved {} reviews with rating: {}", response.size(), rating);
        return ResponseEntity.ok(response);
    }

    /**
     * Updates a review.
     * 
     * @param reviewId the review ID
     * @param userId the user ID attempting the update
     * @param updateDTO the update data
     * @return ResponseEntity with updated review and HTTP 200 status
     */
    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResponseDTO> updateReview(
            @PathVariable Long reviewId,
            @RequestParam Long userId,
            @Valid @RequestBody ReviewUpdateDTO updateDTO) {
        log.info("PUT /api/v1/reviews/{} - Updating review by user ID: {}", reviewId, userId);
        
        ReviewResponseDTO response = reviewService.updateReview(reviewId, userId, updateDTO);
        
        log.info("Successfully updated review ID: {}", reviewId);
        return ResponseEntity.ok(response);
    }

    /**
     * Moderates a review (approve or reject).
     * 
     * @param reviewId the review ID
     * @param moderationDTO the moderation data
     * @return ResponseEntity with moderated review and HTTP 200 status
     */
    @PatchMapping("/{reviewId}/moderate")
    public ResponseEntity<ReviewResponseDTO> moderateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewModerationDTO moderationDTO) {
        log.info("PATCH /api/v1/reviews/{}/moderate - Moderating review to status: {}", 
                reviewId, moderationDTO.getStatus());
        
        ReviewResponseDTO response = reviewService.moderateReview(reviewId, moderationDTO);
        
        log.info("Successfully moderated review ID: {}", reviewId);
        return ResponseEntity.ok(response);
    }

    /**
     * Approves a review.
     * 
     * @param reviewId the review ID
     * @param moderatorId the moderator user ID
     * @return ResponseEntity with approved review and HTTP 200 status
     */
    @PatchMapping("/{reviewId}/approve")
    public ResponseEntity<ReviewResponseDTO> approveReview(
            @PathVariable Long reviewId,
            @RequestParam Long moderatorId) {
        log.info("PATCH /api/v1/reviews/{}/approve - Approving review by moderator ID: {}", 
                reviewId, moderatorId);
        
        ReviewResponseDTO response = reviewService.approveReview(reviewId, moderatorId);
        
        log.info("Successfully approved review ID: {}", reviewId);
        return ResponseEntity.ok(response);
    }

    /**
     * Rejects a review.
     * 
     * @param reviewId the review ID
     * @param moderatorId the moderator user ID
     * @param reason the rejection reason
     * @return ResponseEntity with rejected review and HTTP 200 status
     */
    @PatchMapping("/{reviewId}/reject")
    public ResponseEntity<ReviewResponseDTO> rejectReview(
            @PathVariable Long reviewId,
            @RequestParam Long moderatorId,
            @RequestParam String reason) {
        log.info("PATCH /api/v1/reviews/{}/reject - Rejecting review by moderator ID: {}", 
                reviewId, moderatorId);
        
        ReviewResponseDTO response = reviewService.rejectReview(reviewId, moderatorId, reason);
        
        log.info("Successfully rejected review ID: {}", reviewId);
        return ResponseEntity.ok(response);
    }

    /**
     * Marks a review as helpful.
     * 
     * @param reviewId the review ID
     * @return ResponseEntity with updated review and HTTP 200 status
     */
    @PatchMapping("/{reviewId}/helpful")
    public ResponseEntity<ReviewResponseDTO> markReviewAsHelpful(@PathVariable Long reviewId) {
        log.info("PATCH /api/v1/reviews/{}/helpful - Marking review as helpful", reviewId);
        
        ReviewResponseDTO response = reviewService.markReviewAsHelpful(reviewId);
        
        log.info("Successfully marked review ID: {} as helpful", reviewId);
        return ResponseEntity.ok(response);
    }

    /**
     * Gets rating statistics for a book.
     * 
     * @param bookId the book ID
     * @return ResponseEntity with rating statistics and HTTP 200 status
     */
    @GetMapping("/book/{bookId}/stats")
    public ResponseEntity<BookRatingStatsDTO> getBookRatingStats(@PathVariable Long bookId) {
        log.info("GET /api/v1/reviews/book/{}/stats - Fetching rating statistics", bookId);
        
        BookRatingStatsDTO response = reviewService.getBookRatingStats(bookId);
        
        log.info("Successfully retrieved rating stats for book ID: {}", bookId);
        return ResponseEntity.ok(response);
    }

    /**
     * Calculates average rating for a book.
     * 
     * @param bookId the book ID
     * @return ResponseEntity with average rating and HTTP 200 status
     */
    @GetMapping("/book/{bookId}/average-rating")
    public ResponseEntity<Double> calculateAverageRating(@PathVariable Long bookId) {
        log.info("GET /api/v1/reviews/book/{}/average-rating - Calculating average rating", bookId);
        
        Double averageRating = reviewService.calculateAverageRating(bookId);
        
        log.info("Average rating for book ID {}: {}", bookId, averageRating);
        return ResponseEntity.ok(averageRating);
    }

    /**
     * Deletes a review.
     * 
     * @param reviewId the review ID
     * @param userId the user ID attempting deletion
     * @return ResponseEntity with HTTP 204 No Content status
     */
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Long reviewId,
            @RequestParam Long userId) {
        log.info("DELETE /api/v1/reviews/{} - Deleting review by user ID: {}", reviewId, userId);
        
        reviewService.deleteReview(reviewId, userId);
        
        log.info("Successfully deleted review ID: {}", reviewId);
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
        log.info("DELETE /api/v1/reviews/{}/admin - Admin deleting review", reviewId);
        
        reviewService.deleteReviewByAdmin(reviewId);
        
        log.info("Successfully deleted review ID: {} by admin", reviewId);
        return ResponseEntity.noContent().build();
    }
}