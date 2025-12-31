package com.book.management.authentication.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * RefreshToken Entity
 * 
 * Represents a refresh token used to obtain new access tokens.
 * 
 * Purpose:
 * - Track issued refresh tokens
 * - Prevent refresh token reuse (rotation)
 * - Enable refresh token revocation
 * 
 * Security Features:
 * - One-time use (deleted after refresh)
 * - Long expiration (7 days typical)
 * - Can be revoked on logout
 * 
 * Storage Strategy:
 * - Production: Redis with 7-day TTL
 * - Development: In-memory map
 * 
 * @author Aditya Srivastava
 * @version 2.0 - REST API Compliant
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    /**
     * Unique identifier for the refresh token
     */
    private String id;

    /**
     * The actual refresh token string
     */
    private String token;

    /**
     * User ID who owns this refresh token
     */
    private String userId;

    /**
     * When the refresh token was created
     */
    private LocalDateTime createdAt;

    /**
     * When the refresh token expires
     */
    private LocalDateTime expiresAt;

    /**
     * IP address where token was issued (optional, for security)
     */
    private String ipAddress;

    /**
     * User agent where token was issued (optional, for security)
     */
    private String userAgent;

    /**
     * Whether token has been used (for rotation detection)
     */
    @Builder.Default
    private boolean used = false;

    /**
     * When the token was last used
     */
    private LocalDateTime usedAt;
}
