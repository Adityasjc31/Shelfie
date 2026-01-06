package com.book.management.authentication.service.impl;

import com.book.management.authentication.client.UserServiceClient;
import com.book.management.authentication.dto.*;
import com.book.management.authentication.exception.*;
import com.book.management.authentication.model.BlacklistedToken;
import com.book.management.authentication.model.RefreshToken;
import com.book.management.authentication.repository.ITokenBlacklistRepository;
import com.book.management.authentication.repository.IRefreshTokenRepository;
import com.book.management.authentication.service.IAuthenticationService;
import com.book.management.authentication.util.JwtUtil;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Authentication Service Implementation - Stateless REST API
 *
 * Implements stateless JWT authentication without server-side sessions.
 *
 * Key Features:
 * - Stateless authentication (REST compliant)
 * - JWT token generation and validation
 * - Token blacklist for logout
 * - Refresh token rotation
 * - Integration with User Service via FeignClient
 *
 * Storage:
 * - NO sessions stored
 * - Blacklisted tokens only (until expiry)
 * - Refresh tokens for rotation
 *
 * @author Aditya Srivastava
 * @version 2.0 - FeignClient Integration
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService implements IAuthenticationService {

    private final JwtUtil jwtUtil;
    private final ITokenBlacklistRepository tokenBlacklistRepository;
    private final IRefreshTokenRepository refreshTokenRepository;
    private final UserServiceClient userServiceClient;

    /**
     * {@inheritDoc}
     */
    @Override
    public AuthResponse register(RegisterRequest request) {
        log.info("Processing registration for email: {}", request.getEmail());

        // Build registration DTO
        UserRegistrationDTO userDTO = UserRegistrationDTO.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(request.getPassword())
                .role(request.getRole())
                .build();

        try {
            // Call User Service via FeignClient
            UserResponseDTO user = userServiceClient.registerUser(userDTO);

            if (user == null) {
                throw new AuthenticationException("User registration failed");
            }

            log.info("User registered successfully: {}", user.getUserId());

            // Generate JWT tokens (no session created)
            return generateAuthResponse(user);

        } catch (UserServiceException e) {
            log.error("User service unavailable during registration: {}", e.getMessage());
            throw new ServiceUnavailableException("User Service is temporarily unavailable. Please try again later.");
        } catch (FeignException.ServiceUnavailable | FeignException.BadGateway | FeignException.GatewayTimeout e) {
            log.error("User service connection failed during registration: {}", e.getMessage());
            throw new ServiceUnavailableException("User Service is temporarily unavailable. Please try again later.");
        } catch (FeignException e) {
            log.error("Feign client error during registration: {}", e.getMessage());
            if (e.status() == 409) {
                throw new UserAlreadyExistsException(request.getEmail());
            }
            throw new ServiceUnavailableException("User Service is temporarily unavailable. Please try again later.");
        } catch (UserAlreadyExistsException e) {
            throw e;
        } catch (Exception e) {
            log.error("Registration failed: {}", e.getMessage());
            throw new AuthenticationException("Registration failed: " + e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AuthResponse login(LoginRequest request) {
        log.info("Processing login for email: {}", request.getEmail());

        // Build login DTO
        UserLoginDTO loginDTO = UserLoginDTO.builder()
                .email(request.getEmail())
                .password(request.getPassword())
                .build();

        try {
            // Call User Service via FeignClient
            UserResponseDTO user = userServiceClient.loginUser(loginDTO);

            if (user == null) {
                throw new InvalidCredentialsException();
            }

            log.info("User authenticated successfully: {}", user.getUserId());

            // Generate JWT tokens (stateless - no session)
            return generateAuthResponse(user);

        } catch (UserServiceException e) {
            log.error("User service unavailable during login: {}", e.getMessage());
            throw new ServiceUnavailableException("User Service is temporarily unavailable. Please try again later.");
        } catch (FeignException.ServiceUnavailable | FeignException.BadGateway | FeignException.GatewayTimeout e) {
            log.error("User service connection failed during login: {}", e.getMessage());
            throw new ServiceUnavailableException("User Service is temporarily unavailable. Please try again later.");
        } catch (FeignException e) {
            log.error("Feign client error during login: {}", e.getMessage());
            if (e.status() == 401 || e.status() == 404) {
                throw new InvalidCredentialsException();
            }
            throw new ServiceUnavailableException("User Service is temporarily unavailable. Please try again later.");
        } catch (InvalidCredentialsException e) {
            throw e;
        } catch (Exception e) {
            log.error("Login failed: {}", e.getMessage());
            throw new InvalidCredentialsException();
        }
    }

    /**
     * {@inheritDoc}
     *
     * Performs STATELESS validation:
     * 1. Cryptographic JWT validation (signature, expiry)
     * 2. Check if token is blacklisted (logout)
     * 3. Extract user info from JWT claims (no database lookup)
     */
    @Override
    public TokenValidationResponse validateToken(String token) {
        log.debug("Validating token");

        try {
            // 1. Validate JWT cryptographically (fast, no database)
            if (!jwtUtil.validateToken(token)) {
                return buildInvalidResponse("Invalid or expired token");
            }

            // 2. Check if token is blacklisted (logout)
            if (tokenBlacklistRepository.existsByToken(token)) {
                return buildInvalidResponse("Token has been revoked");
            }

            // 3. Extract user info from JWT claims (no database lookup)
            String userId = jwtUtil.extractUserId(token);
            String username = jwtUtil.extractUsername(token);
            List<String> roles = jwtUtil.extractRoles(token);

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
            return buildInvalidResponse("Token validation failed");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        log.info("Processing token refresh");

        try {
            String refreshTokenStr = request.getRefreshToken();

            // 1. Validate refresh token
            if (!jwtUtil.validateToken(refreshTokenStr)) {
                throw new InvalidTokenException("Invalid or expired refresh token");
            }

            // 2. Check token type
            String tokenType = jwtUtil.extractTokenType(refreshTokenStr);
            if (!"REFRESH".equals(tokenType)) {
                throw new InvalidTokenException("Not a valid refresh token");
            }

            // 3. Check if refresh token exists and not used
            RefreshToken storedToken = refreshTokenRepository
                    .findByToken(refreshTokenStr)
                    .orElseThrow(() -> new InvalidTokenException("Refresh token not found"));

            if (storedToken.isUsed()) {
                // Possible token reuse attack - invalidate all user tokens
                log.warn("Refresh token reuse detected for user: {}", storedToken.getUserId());
                logoutFromAllDevices(storedToken.getUserId());
                throw new InvalidTokenException("Refresh token has already been used");
            }

            // 4. Mark refresh token as used (rotation)
            storedToken.setUsed(true);
            storedToken.setUsedAt(LocalDateTime.now());
            refreshTokenRepository.save(storedToken);

            // 5. Get user details via FeignClient
            String userId = jwtUtil.extractUserId(refreshTokenStr);
            UserResponseDTO user = getUserById(userId);

            // 6. Generate new tokens
            log.info("Token refreshed successfully for user: {}", userId);
            return generateAuthResponse(user);

        } catch (InvalidTokenException e) {
            throw e;
        } catch (Exception e) {
            log.error("Token refresh failed: {}", e.getMessage());
            throw new InvalidTokenException("Token refresh failed: " + e.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     *
     * Blacklists the token until it expires naturally.
     */
    @Override
    public void logout(String token) {
        log.info("Processing logout");

        try {
            String userId = jwtUtil.extractUserId(token);
            LocalDateTime expiresAt = jwtUtil.extractExpiration(token)
                    .toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDateTime();

            // Add token to blacklist
            BlacklistedToken blacklistedToken = BlacklistedToken.builder()
                    .id(UUID.randomUUID().toString())
                    .token(token)
                    .userId(userId)
                    .blacklistedAt(LocalDateTime.now())
                    .expiresAt(expiresAt)
                    .reason("USER_LOGOUT")
                    .build();

            tokenBlacklistRepository.save(blacklistedToken);

            log.info("User logged out successfully: {}", userId);

        } catch (Exception e) {
            log.error("Logout failed: {}", e.getMessage());
            throw new AuthenticationException("Logout failed");
        }
    }

    /**
     * {@inheritDoc}
     *
     * Invalidates all tokens for a user.
     */
    @Override
    public void logoutFromAllDevices(String userId) {
        log.info("Logging out user from all devices: {}", userId);

        try {
            // Delete all refresh tokens for user
            refreshTokenRepository.deleteByUserId(userId);

            // Note: Cannot blacklist all access tokens as we don't store them
            // They will expire naturally based on JWT expiration time

            log.info("User logged out from all devices: {}", userId);

        } catch (Exception e) {
            log.error("Logout from all devices failed: {}", e.getMessage());
            throw new AuthenticationException("Logout from all devices failed");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTokenBlacklisted(String token) {
        return tokenBlacklistRepository.existsByToken(token);
    }

    /**
     * Generates authentication response with JWT tokens.
     *
     * Creates:
     * - Access token (24 hours)
     * - Refresh token (7 days)
     * - Stores refresh token for rotation
     */
    private AuthResponse generateAuthResponse(UserResponseDTO user) {
        String userId = user.getUserId().toString();
        String email = user.getEmail();
        List<String> roles = Arrays.asList(user.getRole());

        // Generate JWT tokens
        String accessToken = jwtUtil.generateToken(userId, email, roles);
        String refreshToken = jwtUtil.generateRefreshToken(userId, email);

        // Store refresh token for rotation tracking
        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .id(UUID.randomUUID().toString())
                .token(refreshToken)
                .userId(userId)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusSeconds(jwtUtil.getRefreshExpirationTime() / 1000))
                .used(false)
                .build();

        refreshTokenRepository.save(refreshTokenEntity);

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
     * Builds invalid token validation response.
     */
    private TokenValidationResponse buildInvalidResponse(String message) {
        return TokenValidationResponse.builder()
                .valid(false)
                .message(message)
                .validatedAt(LocalDateTime.now())
                .build();
    }

    /**
     * Gets user details from User Service via FeignClient.
     */
    private UserResponseDTO getUserById(String userId) {
        try {
            return userServiceClient.getUserById(Long.parseLong(userId));
        } catch (UserServiceException e) {
            log.error("Failed to get user from User Service: {}", e.getMessage());
            throw new ServiceUnavailableException("User Service is temporarily unavailable. Please try again later.");
        } catch (FeignException e) {
            log.error("Feign client error while getting user: {}", e.getMessage());
            throw new ServiceUnavailableException("User Service is temporarily unavailable. Please try again later.");
        }
    }
}
