package com.db.ms.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity class representing a User in the Digital Bookstore system.
 * 
 * @author Digital Bookstore Team
 * @version 1.0
 * @since 2024-01-01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    /**
     * Unique identifier for the user.
     */
    private Long userId;

    /**
     * Full name of the user.
     */
    private String name;

    /**
     * Email address of the user (used for authentication).
     * Must be unique.
     */
    private String email;

    /**
     * Encrypted password for user authentication.
     * Stored as BCrypt hash.
     */
    private String password;

    /**
     * Role of the user in the system.
     * Can be either CUSTOMER or ADMIN.
     */
    private UserRole role;

    /**
     * Indicates whether the user account is active.
     * Inactive accounts cannot log in.
     */
    @Builder.Default
    private Boolean isActive = true;

    /**
     * Timestamp when the user account was created.
     */
    private LocalDateTime createdAt;

    /**
     * Timestamp when the user account was last updated.
     */
    private LocalDateTime updatedAt;
}
