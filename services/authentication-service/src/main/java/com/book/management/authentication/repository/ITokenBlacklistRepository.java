package com.book.management.authentication.repository;

import com.book.management.authentication.model.BlacklistedToken;

import java.util.List;
import java.util.Optional;

/**
 * Token Blacklist Repository Interface
 *
 * Manages revoked/blacklisted JWT tokens.
 *
 * Design:
 * - Store only blacklisted tokens (logout)
 * - Auto-cleanup expired entries
 * - Fast lookup for validation
 *
 * Production: Use Redis with TTL for automatic expiration
 * Development: In-memory storage acceptable
 *
 * @author Aditya Srivastava
 * @version 2.0 - REST API Compliant
 */
public interface ITokenBlacklistRepository {

    /**
     * Adds token to blacklist.
     *
     * @param token blacklisted token entity
     * @return saved token
     */
    BlacklistedToken save(BlacklistedToken token);

    /**
     * Checks if token exists in blacklist.
     *
     * @param token JWT token string
     * @return true if blacklisted, false otherwise
     */
    boolean existsByToken(String token);

    /**
     * Finds blacklisted token by token string.
     *
     * @param token JWT token string
     * @return Optional containing blacklisted token if found
     */
    Optional<BlacklistedToken> findByToken(String token);

    /**
     * Finds all blacklisted tokens for a user.
     * Useful for "logout from all devices".
     *
     * @param userId user ID
     * @return list of blacklisted tokens
     */
    List<BlacklistedToken> findByUserId(String userId);

    /**
     * Deletes expired blacklisted tokens.
     * Should be called periodically via scheduled task.
     *
     * @return number of tokens deleted
     */
    int deleteExpiredTokens();

    /**
     * Deletes all blacklisted tokens for a user.
     *
     * @param userId user ID
     */
    void deleteByUserId(String userId);

    /**
     * Counts total blacklisted tokens.
     * Useful for monitoring.
     *
     * @return count of blacklisted tokens
     */
    long count();
}