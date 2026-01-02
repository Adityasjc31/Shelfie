package com.book.management.authentication.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime; /**
 * BlacklistedToken Entity
 * 
 * Represents a revoked/blacklisted JWT token.
 * 
 * Purpose:
 * - Track logged-out tokens until they expire naturally
 * - Enable immediate token invalidation
 * - Support "logout from all devices" feature
 * 
 * Storage Strategy:
 * - Production: Redis with TTL (automatic expiration)
 * - Development: In-memory map (manual cleanup)
 * 
 * @author Aditya Srivastava
 * @version 1.0 - REST API Compliant
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlacklistedToken {

    /**
     * Unique identifier for the blacklist entry
     */
    private String id;

    /**
     * The actual JWT token string that is blacklisted
     */
    private String token;

    /**
     * User ID who owned this token
     */
    private String userId;

    /**
     * When the token was blacklisted (logout time)
     */
    private LocalDateTime blacklistedAt;

    /**
     * When the token expires (JWT expiration)
     * After this time, token can be removed from blacklist
     */
    private LocalDateTime expiresAt;

    /**
     * Reason for blacklisting (optional)
     * Examples: "USER_LOGOUT", "ADMIN_REVOKE", "SECURITY_BREACH"
     */
    private String reason;
}
