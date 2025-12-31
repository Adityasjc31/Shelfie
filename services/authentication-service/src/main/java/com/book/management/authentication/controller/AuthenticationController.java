package com.book.management.authentication.controller;

import com.book.management.authentication.dto.*;
import com.book.management.authentication.service.AuthenticationService;
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
 * Endpoints:
 * - POST /api/v1/auth/register - Register new user
 * - POST /api/v1/auth/login - Login user
 * - POST /api/v1/auth/validate - Validate JWT token
 * - POST /api/v1/auth/refresh - Refresh access token
 * - POST /api/v1/auth/logout - Logout user
 * 
 * @author Aditya Srivastava
 * @version 1.0
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    /**
     * Registers a new user.
     * 
     * POST /api/v1/auth/register
     * 
     * @param request registration details
     * @return authentication response with tokens
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
     * @return authentication response with tokens
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request received for: {}", request.getEmail());
        
        AuthResponse response = authenticationService.login(request);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Validates JWT token.
     * Used by API Gateway to verify authentication.
     * 
     * POST /api/v1/auth/validate
     * 
     * @param authHeader Authorization header with Bearer token
     * @return validation response
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
     * @param request refresh token request
     * @return new authentication response
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("Token refresh request received");
        
        AuthResponse response = authenticationService.refreshToken(request);
        
        return ResponseEntity.ok(response);
    }

    /**
     * Logs out user by invalidating session.
     * 
     * POST /api/v1/auth/logout
     * 
     * @param authHeader Authorization header with Bearer token
     * @return success response
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
            @RequestHeader("Authorization") String authHeader) {
        
        log.info("Logout request received");
        
        String token = authHeader.replace("Bearer ", "").trim();
        authenticationService.logout(token);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logged out successfully");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Health check endpoint.
     * 
     * GET /api/v1/auth/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "authentication-service");
        return ResponseEntity.ok(response);
    }
}
