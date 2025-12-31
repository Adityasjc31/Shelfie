package com.book.management.authentication.service;

import com.book.management.authentication.dto.*;
import com.book.management.authentication.exception.*;
import com.book.management.authentication.model.Session;
import com.book.management.authentication.repository.SessionRepository;
import com.book.management.authentication.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Authentication Service Implementation
 * 
 * Handles all authentication operations including:
 * - User registration
 * - User login with JWT generation
 * - Token validation
 * - Token refresh
 * - Session management
 * 
 * @author Aditya Srivastava
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService implements IAuthenticationService {

    private final JwtUtil jwtUtil;
    private final SessionRepository sessionRepository;
    private final WebClient.Builder webClientBuilder;

    @Value("${user.service.url:lb://user-service}")
    private String userServiceUrl;

    @Value("${session.max-concurrent-sessions:3}")
    private int maxConcurrentSessions;

    /**
     * Registers a new user.
     * Communicates with User Service to create user account.
     * 
     * @param request registration details
     * @return authentication response with tokens
     */
    public AuthResponse register(RegisterRequest request) {
        log.info("Processing registration for email: {}", request.getEmail());

        // Call User Service to register user
        UserRegistrationDTO userDTO = UserRegistrationDTO.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(request.getPassword())
                .role(request.getRole())
                .build();

        try {
            // Call user-service synchronously
            UserResponseDTO user = webClientBuilder.build()
                    .post()
                    .uri(userServiceUrl + "/users/register")
                    .bodyValue(userDTO)
                    .retrieve()
                    .bodyToMono(UserResponseDTO.class)
                    .block();

            if (user == null) {
                throw new AuthenticationException("User registration failed");
            }

            log.info("User registered successfully: {}", user.getUserId());

            // Generate tokens
            return generateAuthResponse(user);

        } catch (Exception e) {
            log.error("Registration failed: {}", e.getMessage());
            throw new AuthenticationException("Registration failed: " + e.getMessage());
        }
    }

    /**
     * Authenticates user and generates JWT tokens.
     * 
     * @param request login credentials
     * @return authentication response with tokens
     */
    public AuthResponse login(LoginRequest request) {
        log.info("Processing login for email: {}", request.getEmail());

        // Call User Service to authenticate
        UserLoginDTO loginDTO = UserLoginDTO.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .build();

        try {
            // Call user-service synchronously
            UserResponseDTO user = webClientBuilder.build()
                    .post()
                    .uri(userServiceUrl + "/users/login")
                    .bodyValue(loginDTO)
                    .retrieve()
                    .bodyToMono(UserResponseDTO.class)
                    .block();

            if (user == null) {
                throw new InvalidCredentialsException();
            }

            log.info("User authenticated successfully: {}", user.getUserId());

            // Check and manage sessions
            manageUserSessions(user.getUserId().toString());

            // Generate tokens and create session
            return generateAuthResponse(user);

        } catch (Exception e) {
            log.error("Login failed: {}", e.getMessage());
            throw new InvalidCredentialsException();
        }
    }

    /**
     * Validates JWT token.
     * 
     * @param token JWT token to validate
     * @return validation response
     */
    public TokenValidationResponse validateToken(String token) {
        log.debug("Validating token");

        try {
            if (!jwtUtil.validateToken(token)) {
                return TokenValidationResponse.builder()
                        .valid(false)
                        .message("Invalid or expired token")
                        .validatedAt(LocalDateTime.now())
                        .build();
            }

            String userId = jwtUtil.extractUserId(token);
            String username = jwtUtil.extractUsername(token);
            List<String> roles = jwtUtil.extractRoles(token);

            // Check if session exists and is valid
            if (!isSessionValid(userId, token)) {
                return TokenValidationResponse.builder()
                        .valid(false)
                        .message("Session expired or invalid")
                        .validatedAt(LocalDateTime.now())
                        .build();
            }

            log.debug("Token validated successfully for user: {}", userId);

            return TokenValidationResponse.builder()
                    .valid(true)
                    .userId(userId)
                    .username(username)
                    .roles(roles)
                    .message("Token is valid")
                    .validatedAt(LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            log.error("Token validation error: {}", e.getMessage());
            return TokenValidationResponse.builder()
                    .valid(false)
                    .message("Token validation failed")
                    .validatedAt(LocalDateTime.now())
                    .build();
        }
    }

    /**
     * Refreshes access token using refresh token.
     * 
     * @param request refresh token request
     * @return new authentication response
     */
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        log.info("Processing token refresh");

        try {
            String refreshToken = request.getRefreshToken();

            if (!jwtUtil.validateToken(refreshToken)) {
                throw new InvalidTokenException("Invalid or expired refresh token");
            }

            String tokenType = jwtUtil.extractTokenType(refreshToken);
            if (!"REFRESH".equals(tokenType)) {
                throw new InvalidTokenException("Not a valid refresh token");
            }

            String userId = jwtUtil.extractUserId(refreshToken);
            String email = jwtUtil.extractUsername(refreshToken);

            // Get user details from User Service
            UserResponseDTO user = getUserById(userId);

            // Generate new tokens
            return generateAuthResponse(user);

        } catch (Exception e) {
            log.error("Token refresh failed: {}", e.getMessage());
            throw new InvalidTokenException("Token refresh failed");
        }
    }

    /**
     * Logs out user by invalidating session.
     * 
     * @param token JWT token
     */
    public void logout(String token) {
        try {
            String userId = jwtUtil.extractUserId(token);
            sessionRepository.deleteByUserIdAndToken(userId, token);
            log.info("User logged out successfully: {}", userId);
        } catch (Exception e) {
            log.error("Logout failed: {}", e.getMessage());
        }
    }

    @Override
    public void logoutFromAllDevices(String userId) {

    }

    @Override
    public boolean isTokenBlacklisted(String token) {
        return false;
    }

    /**
     * Generates authentication response with tokens and user info.
     */
    private AuthResponse generateAuthResponse(UserResponseDTO user) {
        String userId = user.getUserId().toString();
        String email = user.getEmail();
        List<String> roles = Arrays.asList(user.getRole().toString());

        // Generate tokens
        String accessToken = jwtUtil.generateToken(userId, email, roles);
        String refreshToken = jwtUtil.generateRefreshToken(userId, email);

        // Create session
        Session session = Session.builder()
                .sessionId(UUID.randomUUID().toString())
                .userId(userId)
                .token(accessToken)
                .refreshToken(refreshToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusSeconds(jwtUtil.getExpirationTime() / 1000))
                .build();

        sessionRepository.save(session);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtUtil.getExpirationTime())
                .user(AuthResponse.UserInfo.builder()
                        .userId(userId)
                        .email(email)
                        .name(user.getName())
                        .roles(roles)
                        .build())
                .issuedAt(LocalDateTime.now())
                .build();
    }

    /**
     * Manages user sessions - limits concurrent sessions per user.
     */
    private void manageUserSessions(String userId) {
        List<Session> userSessions = sessionRepository.findByUserId(userId);

        if (userSessions.size() >= maxConcurrentSessions) {
            // Remove oldest session
            Session oldestSession = userSessions.stream()
                    .sorted((s1, s2) -> s1.getCreatedAt().compareTo(s2.getCreatedAt()))
                    .findFirst()
                    .orElse(null);

            if (oldestSession != null) {
                sessionRepository.delete(oldestSession.getSessionId());
                log.info("Removed oldest session for user: {}", userId);
            }
        }
    }

    /**
     * Checks if session is valid.
     */
    private boolean isSessionValid(String userId, String token) {
        return sessionRepository.findByUserIdAndToken(userId, token)
                .map(session -> session.getExpiresAt().isAfter(LocalDateTime.now()))
                .orElse(false);
    }

    /**
     * Gets user details from User Service.
     */
    private UserResponseDTO getUserById(String userId) {
        return webClientBuilder.build()
                .get()
                .uri(userServiceUrl + "/users/" + userId)
                .retrieve()
                .bodyToMono(UserResponseDTO.class)
                .block();
    }

    // Inner DTOs for User Service communication
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private static class UserRegistrationDTO {
        private String name;
        private String email;
        private String password;
        private String role;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private static class UserLoginDTO {
        private String email;
        private String password;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    private static class UserResponseDTO {
        private Long userId;
        private String name;
        private String email;
        private Object role; // UserRole enum
        private Boolean isActive;
    }
}
