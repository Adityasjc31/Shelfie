package com.book.management.api_gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration for Role-Based Access Control (RBAC).
 * 
 * Defines which roles are required to access specific endpoints.
 * Supports wildcard patterns for path matching.
 * 
 * Configuration example in application.yml:
 * rbac:
 *   enabled: true
 *   rules:
 *     - path: "/api/v1/users"
 *       methods: ["GET"]
 *       roles: ["ADMIN"]
 *     - path: "/api/v1/book/add"
 *       methods: ["POST"]
 *       roles: ["ADMIN"]
 * 
 * @author Aditya Srivastava
 * @version 1.0
 * @since 2026-01-07
 */
@Configuration
@ConfigurationProperties(prefix = "rbac")
@Data
public class RoleAuthorizationConfig {

    /**
     * Whether role-based access control is enabled.
     */
    private boolean enabled = true;

    /**
     * List of authorization rules.
     */
    private List<AuthorizationRule> rules;

    /**
     * Represents a single authorization rule.
     */
    @Data
    public static class AuthorizationRule {
        /**
         * Path pattern to match (supports wildcards like /api/v1/users/**)
         */
        private String path;

        /**
         * HTTP methods this rule applies to (GET, POST, PUT, DELETE, PATCH).
         * If empty, applies to all methods.
         */
        private List<String> methods;

        /**
         * Roles allowed to access this endpoint.
         * User must have at least one of these roles.
         */
        private List<String> roles;

        /**
         * Whether all listed roles are required (AND) or just one (OR).
         * Default is false (OR - user needs at least one role).
         */
        private boolean requireAll = false;
    }
}
