package com.book.management.api_gateway.service;

import com.book.management.api_gateway.config.RoleAuthorizationConfig;
import com.book.management.api_gateway.config.RoleAuthorizationConfig.AuthorizationRule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import java.util.List;

/**
 * Service for Role-Based Authorization.
 * 
 * Checks if a user with specific roles can access a given endpoint.
 * Uses Ant-style path matching for flexible rule configuration.
 * 
 * @author Aditya Srivastava
 * @version 1.0
 * @since 2026-01-07
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RoleAuthorizationService {

    private final RoleAuthorizationConfig rbacConfig;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    /**
     * Checks if a user with the given roles can access the specified path with the given method.
     * 
     * @param path       the request path
     * @param method     the HTTP method
     * @param userRoles  the user's roles
     * @return true if access is allowed, false otherwise
     */
    public boolean isAuthorized(String path, HttpMethod method, List<String> userRoles) {
        // If RBAC is disabled, allow all
        if (!rbacConfig.isEnabled()) {
            log.debug("RBAC is disabled, allowing access to: {}", path);
            return true;
        }

        // If no rules defined, allow all authenticated users
        if (rbacConfig.getRules() == null || rbacConfig.getRules().isEmpty()) {
            log.debug("No RBAC rules defined, allowing access to: {}", path);
            return true;
        }

        // Find matching rule for this path and method
        AuthorizationRule matchingRule = findMatchingRule(path, method);

        // If no matching rule, allow access (authenticated is enough)
        if (matchingRule == null) {
            log.debug("No matching RBAC rule for path: {}, method: {}. Allowing authenticated access.", path, method);
            return true;
        }

        // Check if user has required roles
        boolean authorized = checkRoles(userRoles, matchingRule);
        
        if (authorized) {
            log.debug("✓ User authorized for path: {} with roles: {}", path, userRoles);
        } else {
            log.warn("✗ User NOT authorized for path: {}. Required roles: {}, User roles: {}", 
                    path, matchingRule.getRoles(), userRoles);
        }

        return authorized;
    }

    /**
     * Finds the first matching authorization rule for the given path and method.
     * 
     * @param path   the request path
     * @param method the HTTP method
     * @return the matching rule, or null if none found
     */
    private AuthorizationRule findMatchingRule(String path, HttpMethod method) {
        for (AuthorizationRule rule : rbacConfig.getRules()) {
            // Check if path matches
            if (pathMatcher.match(rule.getPath(), path)) {
                // Check if method matches (empty methods list means all methods)
                if (rule.getMethods() == null || rule.getMethods().isEmpty() 
                        || rule.getMethods().contains(method.name())) {
                    return rule;
                }
            }
        }
        return null;
    }

    /**
     * Checks if user has the required roles as per the rule.
     * 
     * @param userRoles the user's roles
     * @param rule      the authorization rule
     * @return true if user has required roles
     */
    private boolean checkRoles(List<String> userRoles, AuthorizationRule rule) {
        if (rule.getRoles() == null || rule.getRoles().isEmpty()) {
            return true; // No specific roles required
        }

        if (userRoles == null || userRoles.isEmpty()) {
            return false; // User has no roles but roles are required
        }

        if (rule.isRequireAll()) {
            // User must have ALL required roles
            return userRoles.containsAll(rule.getRoles());
        } else {
            // User must have at least ONE of the required roles
            return rule.getRoles().stream().anyMatch(userRoles::contains);
        }
    }

    /**
     * Gets a message describing why authorization failed.
     * 
     * @param path      the request path
     * @param method    the HTTP method
     * @param userRoles the user's roles
     * @return descriptive error message
     */
    public String getAuthorizationFailureMessage(String path, HttpMethod method, List<String> userRoles) {
        AuthorizationRule matchingRule = findMatchingRule(path, method);
        if (matchingRule != null) {
            return String.format("Access denied. Required roles: %s. Your roles: %s", 
                    matchingRule.getRoles(), userRoles);
        }
        return "Access denied. Insufficient permissions.";
    }
}
