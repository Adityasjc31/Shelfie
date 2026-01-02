package com.book.management.authentication.service;

import com.book.management.authentication.dto.*; /**
 * Authentication Service Interface
 * 
 * Defines contract for authentication operations in a stateless REST API.
 * 
 * Design Principles:
 * - Stateless authentication using JWT
 * - No session state maintained on server
 * - Token validation is cryptographic (no database lookup)
 * - Minimal token tracking only for logout/revocation
 * 
 * @author Aditya Srivastava
 * @version 1.0 - REST API Compliant
 */
public interface IAuthenticationService {

    /**
     * Registers a new user in the system.
     * 
     * Flow:
     * 1. Validates registration data
     * 2. Calls User Service to create user
     * 3. Generates JWT access + refresh tokens
     * 4. Returns tokens (no session created)
     * 
     * @param request registration details
     * @return authentication response with JWT tokens
     * @throws AuthenticationException if registration fails
     * @throws UserAlreadyExistsException if email already exists
     */
    AuthResponse register(RegisterRequest request);

    /**
     * Authenticates user with email and password.
     * 
     * Flow:
     * 1. Validates credentials with User Service
     * 2. Generates JWT tokens
     * 3. Stores refresh token for revocation tracking
     * 4. Returns tokens (stateless - no session)
     * 
     * @param request login credentials
     * @return authentication response with JWT tokens
     * @throws InvalidCredentialsException if credentials invalid
     */
    AuthResponse login(LoginRequest request);

    /**
     * Validates JWT access token.
     * 
     * This method performs CRYPTOGRAPHIC validation only:
     * 1. Verifies JWT signature
     * 2. Checks expiration
     * 3. Validates issuer
     * 4. Checks if token is blacklisted (logout)
     * 
     * NO database lookup for user data - JWT is self-contained.
     * 
     * @param token JWT access token
     * @return validation response with user info from token claims
     */
    TokenValidationResponse validateToken(String token);

    /**
     * Refreshes access token using refresh token.
     * 
     * Flow:
     * 1. Validates refresh token
     * 2. Checks if refresh token is blacklisted
     * 3. Generates new access token
     * 4. Optionally rotates refresh token (security best practice)
     * 
     * @param request refresh token request
     * @return new authentication response with fresh tokens
     * @throws InvalidTokenException if refresh token invalid/expired
     */
    AuthResponse refreshToken(RefreshTokenRequest request);

    /**
     * Logs out user by blacklisting current token.
     * 
     * Since JWT tokens are stateless and can't be "deleted",
     * we add them to a blacklist until they expire naturally.
     * 
     * Flow:
     * 1. Extract token from request
     * 2. Add to blacklist with expiration time
     * 3. Token becomes invalid for all future requests
     * 
     * @param token JWT access token to invalidate
     */
    void logout(String token);

    /**
     * Logs out user from all devices.
     * 
     * Invalidates all tokens (access + refresh) for a user.
     * Useful for "logout everywhere" feature.
     * 
     * @param userId user ID
     */
    void logoutFromAllDevices(String userId);

    /**
     * Checks if token is blacklisted (revoked).
     * 
     * @param token JWT token
     * @return true if token is blacklisted, false otherwise
     */
    boolean isTokenBlacklisted(String token);
}
