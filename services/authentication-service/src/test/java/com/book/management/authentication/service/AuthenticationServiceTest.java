package com.book.management.authentication.service;

import com.book.management.authentication.client.UserServiceClient;
import com.book.management.authentication.dto.*;
import com.book.management.authentication.exception.InvalidCredentialsException;
import com.book.management.authentication.exception.InvalidTokenException;
import com.book.management.authentication.exception.UserAlreadyExistsException;
import com.book.management.authentication.model.BlacklistedToken;
import com.book.management.authentication.repository.IRefreshTokenRepository;
import com.book.management.authentication.repository.ITokenBlacklistRepository;
import com.book.management.authentication.service.impl.AuthenticationService;
import com.book.management.authentication.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthenticationService.
 * Tests authentication business logic with JUnit 5 and Mockito.
 * 
 * @author Aditya Srivastava
 * @version 1.0
 * @since 2024-12-29
 */
@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @Mock
    private UserServiceClient userServiceClient;

    @Mock
    private IRefreshTokenRepository refreshTokenRepository;

    @Mock
    private ITokenBlacklistRepository tokenBlacklistRepository;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthenticationService authenticationService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private UserResponseDTO userResponse;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .name("Test User")
                .email("test@example.com")
                .password("password123")
                .role("CUSTOMER")
                .build();

        loginRequest = LoginRequest.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        userResponse = UserResponseDTO.builder()
                .userId(1001L)
                .name("Test User")
                .email("test@example.com")
                .role("CUSTOMER")
                .build();
    }

    @Test
    void testRegister_Success() {
        // Arrange
        when(userServiceClient.registerUser(any(UserRegistrationDTO.class)))
                .thenReturn(userResponse);

        // Act
        AuthResponse response = authenticationService.register(registerRequest);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getAccessToken());
        assertNotNull(response.getRefreshToken());
        assertEquals("test@example.com", response.getUser().getEmail());
        verify(userServiceClient, times(1)).registerUser(any(UserRegistrationDTO.class));
    }

    @Test
    void testRegister_UserAlreadyExists() {
        // Arrange
        when(userServiceClient.registerUser(any(UserRegistrationDTO.class)))
                .thenThrow(new UserAlreadyExistsException("User already exists"));

        // Act & Assert
        assertThrows(UserAlreadyExistsException.class, () -> {
            authenticationService.register(registerRequest);
        });
    }

    @Test
    void testLogin_Success() {
        // Arrange
        when(userServiceClient.loginUser(any(UserLoginDTO.class)))
                .thenReturn(userResponse);

        // Act
        AuthResponse response = authenticationService.login(loginRequest);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getAccessToken());
        assertNotNull(response.getRefreshToken());
        assertEquals("Bearer", response.getTokenType());
        verify(userServiceClient, times(1)).loginUser(any(UserLoginDTO.class));
    }

    @Test
    void testLogin_InvalidCredentials() {
        // Arrange
        when(userServiceClient.loginUser(any(UserLoginDTO.class)))
                .thenThrow(new InvalidCredentialsException());

        // Act & Assert
        assertThrows(InvalidCredentialsException.class, () -> {
            authenticationService.login(loginRequest);
        });
    }

    @Test
    void testValidateToken_ValidToken() {
        // Arrange
        String validToken = "valid-jwt-token";
        when(tokenBlacklistRepository.existsByToken(validToken)).thenReturn(false);

        // Act
        TokenValidationResponse response = authenticationService.validateToken(validToken);

        // Assert
        assertNotNull(response);
        // The response validity depends on JWT parsing
        verify(tokenBlacklistRepository, times(1)).existsByToken(validToken);
    }

    @Test
    void testValidateToken_BlacklistedToken() {
        // Arrange
        String blacklistedToken = "blacklisted-token";
        when(tokenBlacklistRepository.existsByToken(blacklistedToken)).thenReturn(true);

        // Act
        TokenValidationResponse response = authenticationService.validateToken(blacklistedToken);

        // Assert
        assertNotNull(response);
        assertFalse(response.isValid());
        assertEquals("Token has been revoked", response.getMessage());
    }

    @Test
    void testRefreshToken_Success() {
        // Arrange
        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken("valid-refresh-token")
                .build();

        when(refreshTokenRepository.findByToken("valid-refresh-token"))
                .thenReturn(Optional.of(com.book.management.authentication.model.RefreshToken.builder()
                        .token("valid-refresh-token")
                        .userId("1001")
                        .used(false)
                        .build()));
        when(userServiceClient.getUserById(1001L)).thenReturn(userResponse);

        // Act
        AuthResponse response = authenticationService.refreshToken(request);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getAccessToken());
        assertNotNull(response.getRefreshToken());
    }

    @Test
    void testRefreshToken_InvalidToken() {
        // Arrange
        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken("invalid-refresh-token")
                .build();

        when(refreshTokenRepository.findByToken("invalid-refresh-token"))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(InvalidTokenException.class, () -> {
            authenticationService.refreshToken(request);
        });
    }

    @Test
    void testLogout_Success() {
        // Arrange
        String token = "token-to-logout";
        when(tokenBlacklistRepository.save(any(BlacklistedToken.class))).thenReturn(null);

        // Act
        authenticationService.logout(token);

        // Assert - No exception thrown
        verify(tokenBlacklistRepository, times(1)).save(any(BlacklistedToken.class));
    }

    @Test
    void testLogoutFromAllDevices_Success() {
        // Arrange
        String userId = "1001";
        doNothing().when(refreshTokenRepository).deleteByUserId(userId);

        // Act
        authenticationService.logoutFromAllDevices(userId);

        // Assert
        verify(refreshTokenRepository, times(1)).deleteByUserId(userId);
    }

    @Test
    void testIsTokenBlacklisted_True() {
        // Arrange
        String token = "blacklisted-token";
        when(tokenBlacklistRepository.existsByToken(token)).thenReturn(true);

        // Act
        boolean result = authenticationService.isTokenBlacklisted(token);

        // Assert
        assertTrue(result);
    }

    @Test
    void testIsTokenBlacklisted_False() {
        // Arrange
        String token = "valid-token";
        when(tokenBlacklistRepository.existsByToken(token)).thenReturn(false);

        // Act
        boolean result = authenticationService.isTokenBlacklisted(token);

        // Assert
        assertFalse(result);
    }
}
