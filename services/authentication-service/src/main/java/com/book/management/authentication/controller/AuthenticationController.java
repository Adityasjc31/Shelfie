package com.book.management.authentication.controller;

import com.book.management.authentication.dto.*;
import com.book.management.authentication.service.IAuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for Authentication operations.
 *
 * Provides stateless JWT authentication endpoints:
 * - POST /api/v1/auth/register - Register new user
 * - POST /api/v1/auth/login - Login user
 * - POST /api/v1/auth/validate - Validate JWT token
 * - POST /api/v1/auth/refresh - Refresh access token
 * - POST /api/v1/auth/logout - Logout (blacklist token)
 *
 * @author Aditya Srivastava
 * @version 1.0 - REST API Compliant
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AuthenticationController {

    private final IAuthenticationService authenticationService;

    /**
     * Registers a new user.
     *
     * POST /api/v1/auth/register
     *
     * @param request registration details
     * @return authentication response with JWT tokens
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Registration request received for: {}", request.getEmail());

        AuthResponse response = authenticationService.register(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Authenticates user and generates JWT tokens.
     *
     * POST /api/v1/auth/login
     *
     * @param request login credentials
     * @return authentication response with JWT tokens
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request received for: {}", request.getEmail());

        AuthResponse response = authenticationService.login(request);

        return ResponseEntity.ok(response);
    }

    /**
     * Validates JWT token (stateless).
     *
     * Used by API Gateway to verify authentication.
     * Performs cryptographic validation + blacklist check.
     *
     * POST /api/v1/auth/validate
     *
     * @param authHeader Authorization header with Bearer token
     * @return validation response with user info from token
     */
    @PostMapping("/validate")
    public ResponseEntity<TokenValidationResponse> validateToken(
            @RequestHeader("Authorization") String authHeader) {

        log.debug("Token validation request received");

        // Extract token from "Bearer <token>"
        String token = authHeader.replace("Bearer ", "").trim();

        TokenValidationResponse response = authenticationService.validateToken(token);

        return ResponseEntity.ok(response);
    }

    /**
     * Refreshes access token using refresh token.
     *
     * POST /api/v1/auth/refresh
     *
     * Implements token rotation for security:
     * - Old refresh token is marked as used
     * - New access + refresh tokens generated
     * - Detects refresh token reuse attacks
     *
     * @param request refresh token request
     * @return new authentication response with fresh tokens
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("Token refresh request received");

        AuthResponse response = authenticationService.refreshToken(request);

        return ResponseEntity.ok(response);
    }

    /**
     * Logs out user by blacklisting current token.
     *
     * POST /api/v1/auth/logout
     *
     * Since JWT tokens are stateless and can't be "deleted",
     * we add them to a blacklist until they expire naturally.
     *
     * @param authHeader Authorization header with Bearer token
     * @return success message
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
            @RequestHeader("Authorization") String authHeader) {

        log.info("Logout request received");

        String token = authHeader.replace("Bearer ", "").trim();
        authenticationService.logout(token);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Logged out successfully");
        response.put("status", "success");

        return ResponseEntity.ok(response);
    }


    /**
     * Health check endpoint.
     *
     * GET /api/v1/auth/health
     *
     * @return service health status
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "authentication-service");
        response.put("version", "2.0");
        response.put("type", "stateless-jwt");
        return ResponseEntity.ok(response);
    }

    /**
     * Get service info (for debugging).
     *
     * GET /api/v1/auth/info
     *
     * @return service information
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> response = new HashMap<>();
        response.put("service", "authentication-service");
        response.put("version", "2.0");
        response.put("type", "stateless-jwt");
        response.put("features", java.util.List.of(
                "JWT Authentication",
                "Token Refresh",
                "Token Blacklist",
                "Refresh Token Rotation"
        ));
        return ResponseEntity.ok(response);
    }
}
