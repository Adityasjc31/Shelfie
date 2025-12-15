package com.db.ms.Review_Rating.repository;

import java.util.List;
import java.util.Optional;

import com.db.ms.Review_Rating.model.Review;
import com.db.ms.Review_Rating.model.Review.ReviewStatus;

/**
 * Repository interface for Review entity.
 * Provides data access methods for review management operations.
 * Can be implemented using in-memory storage or JPA.
 * 
 * @author Bookstore Development Team
 * @version 1.0
 * @since 2024-12-15
 */
public interface ReviewRepository {

    /**
     * Saves review record (create or update).
     * 
     * @param review the review to save
     * @return the saved review
     */
    Review save(Review review);

    /**
     * Finds review by ID.
     * 
     * @param reviewId the review ID
     * @return Optional containing the review if found
     */
    Optional<Review> findById(Long reviewId);

    /**
     * Finds all reviews.
     * 
     * @return List of all reviews
     */
    List<Review> findAll();

    /**
     * Finds all reviews for a specific book.
     * 
     * @param bookId the book ID
     * @return List of reviews for the book
     */
    List<Review> findByBookId(Long bookId);

    /**
     * Finds all reviews by a specific user.
     * 
     * @param userId the user ID
     * @return List of reviews by the user
     */
    List<Review> findByUserId(Long userId);

    /**
     * Finds all approved reviews for a book.
     * 
     * @param bookId the book ID
     * @return List of approved reviews
     */
    List<Review> findApprovedReviewsByBookId(Long bookId);

    /**
     * Finds all reviews by status.
     * 
     * @param status the review status
     * @return List of reviews with the given status
     */
    List<Review> findByStatus(ReviewStatus status);

    /**
     * Finds pending reviews (for moderation).
     * 
     * @return List of pending reviews
     */
    List<Review> findPendingReviews();

    /**
     * Finds reviews by rating.
     * 
     * @param rating the rating (1-5)
     * @return List of reviews with the given rating
     */
    List<Review> findByRating(Integer rating);

    /**
     * Calculates average rating for a book.
     * 
     * @param bookId the book ID
     * @return average rating, or 0.0 if no reviews
     */
    Double calculateAverageRating(Long bookId);

    /**
     * Counts total reviews for a book.
     * 
     * @param bookId the book ID
     * @return count of reviews
     */
    Long countReviewsByBookId(Long bookId);

    /**
     * Counts approved reviews for a book.
     * 
     * @param bookId the book ID
     * @return count of approved reviews
     */
    Long countApprovedReviewsByBookId(Long bookId);

    /**
     * Checks if a user has already reviewed a book.
     * 
     * @param userId the user ID
     * @param bookId the book ID
     * @return true if user has reviewed the book
     */
    boolean existsByUserIdAndBookId(Long userId, Long bookId);

    /**
     * Checks if review exists by ID.
     * 
     * @param reviewId the review ID
     * @return true if exists
     */
    boolean existsById(Long reviewId);

    /**
     * Deletes review by ID.
     * 
     * @param reviewId the review ID
     */
    void deleteById(Long reviewId);

    /**
     * Deletes all reviews.
     */
    void deleteAll();

    /**
     * Counts total reviews.
     * 
     * @return count of reviews
     */
    long count();
}