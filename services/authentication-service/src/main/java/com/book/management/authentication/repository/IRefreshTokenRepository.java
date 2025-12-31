package com.book.management.authentication.repository;

import com.book.management.authentication.model.RefreshToken;

import java.util.List;
import java.util.Optional;

/**
 * Refresh Token Repository Interface
 *
 * Stores refresh tokens to:
 * - Prevent refresh token reuse
 * - Enable refresh token rotation
 * - Track token usage
 *
 * Production: Use Redis with TTL (7 days typical)
 * Development: In-memory storage acceptable
 *
 * @author Aditya Srivastava
 * @version 1.0 - REST API Compliant
 */
public interface IRefreshTokenRepository {

    /**
     * Saves refresh token.
     *
     * @param refreshToken refresh token entity
     * @return saved refresh token
     */
    RefreshToken save(RefreshToken refreshToken);

    /**
     * Finds refresh token by token string.
     *
     * @param token refresh token string
     * @return Optional containing refresh token if found
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * Finds all refresh tokens for a user.
     *
     * @param userId user ID
     * @return list of refresh tokens
     */
    List<RefreshToken> findByUserId(String userId);

    /**
     * Checks if refresh token exists.
     *
     * @param token refresh token string
     * @return true if exists, false otherwise
     */
    boolean existsByToken(String token);

    /**
     * Deletes refresh token.
     * Used when token is consumed (rotated).
     *
     * @param token refresh token string
     */
    void deleteByToken(String token);

    /**
     * Deletes all refresh tokens for a user.
     * Used for "logout from all devices".
     *
     * @param userId user ID
     */
    void deleteByUserId(String userId);

    /**
     * Deletes expired refresh tokens.
     * Should be called periodically.
     *
     * @return number of tokens deleted
     */
    int deleteExpiredTokens();

    /**
     * Counts active refresh tokens.
     *
     * @return count of active refresh tokens
     */
    long count();

    /**
     * Counts refresh tokens for a user.
     *
     * @param userId user ID
     * @return count of user's refresh tokens
     */
    long countByUserId(String userId);
}
