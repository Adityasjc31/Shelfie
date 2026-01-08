package com.book.management.authentication.controller;

import com.book.management.authentication.dto.*;
import com.book.management.authentication.service.IAuthenticationService;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for AuthenticationController.
 * Uses MockMvc for testing REST endpoints with JUnit 5 and Mockito.
 * 
 * @author Aditya Srivastava
 * @version 1.0
 * @since 2024-12-29
 */
@WebMvcTest(AuthenticationController.class)
public class AuthenticationControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockitoBean
        private IAuthenticationService authenticationService;

        private AuthResponse authResponse;
        private RegisterRequest registerRequest;
        private LoginRequest loginRequest;
        private RefreshTokenRequest refreshTokenRequest;

        @BeforeEach
        void setUp() {
                authResponse = AuthResponse.builder()
                                .accessToken("eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test")
                                .refreshToken("refresh-token-12345")
                                .tokenType("Bearer")
                                .expiresIn(86400L)
                                .user(AuthResponse.UserInfo.builder()
                                                .userId("1001")
                                                .name("testuser")
                                                .email("test@example.com")
                                                .roles(List.of("CUSTOMER"))
                                                .build())
                                .build();

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

                refreshTokenRequest = RefreshTokenRequest.builder()
                                .refreshToken("refresh-token-12345")
                                .build();
        }

        @Test
        void testRegister() throws Exception {
                // Arrange
                when(authenticationService.register(any(RegisterRequest.class)))
                                .thenReturn(authResponse);

                // Act & Assert
                mockMvc.perform(post("/api/v1/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registerRequest)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.accessToken").exists())
                                .andExpect(jsonPath("$.refreshToken").exists())
                                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                                .andExpect(jsonPath("$.user.userId").value("1001"));

                verify(authenticationService, times(1)).register(any(RegisterRequest.class));
        }

        @Test
        void testRegister_InvalidEmail() throws Exception {
                // Arrange
                RegisterRequest invalidRequest = RegisterRequest.builder()
                                .name("Test User")
                                .email("invalid-email")
                                .password("password123")
                                .role("CUSTOMER")
                                .build();

                // Act & Assert
                mockMvc.perform(post("/api/v1/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void testLogin() throws Exception {
                // Arrange
                when(authenticationService.login(any(LoginRequest.class)))
                                .thenReturn(authResponse);

                // Act & Assert
                mockMvc.perform(post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.accessToken").exists())
                                .andExpect(jsonPath("$.refreshToken").exists())
                                .andExpect(jsonPath("$.user.email").value("test@example.com"));

                verify(authenticationService, times(1)).login(any(LoginRequest.class));
        }

        @Test
        void testLogin_MissingPassword() throws Exception {
                // Arrange
                LoginRequest invalidRequest = LoginRequest.builder()
                                .email("test@example.com")
                                .build();

                // Act & Assert
                mockMvc.perform(post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidRequest)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        void testValidateToken() throws Exception {
                // Arrange
                TokenValidationResponse validationResponse = TokenValidationResponse.builder()
                                .valid(true)
                                .userId("1001")
                                .username("testuser")
                                .roles(List.of("CUSTOMER"))
                                .message("Token is valid")
                                .validatedAt(LocalDateTime.now())
                                .build();

                when(authenticationService.validateToken(anyString()))
                                .thenReturn(validationResponse);

                // Act & Assert
                mockMvc.perform(post("/api/v1/auth/validate")
                                .header("Authorization", "Bearer test-token"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.valid").value(true))
                                .andExpect(jsonPath("$.userId").value("1001"));

                verify(authenticationService, times(1)).validateToken(anyString());
        }

        @Test
        void testRefreshToken() throws Exception {
                // Arrange
                when(authenticationService.refreshToken(any(RefreshTokenRequest.class)))
                                .thenReturn(authResponse);

                // Act & Assert
                mockMvc.perform(post("/api/v1/auth/refresh")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(refreshTokenRequest)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.accessToken").exists())
                                .andExpect(jsonPath("$.refreshToken").exists());

                verify(authenticationService, times(1)).refreshToken(any(RefreshTokenRequest.class));
        }

        @Test
        void testLogout() throws Exception {
                // Arrange
                doNothing().when(authenticationService).logout(anyString());

                // Act & Assert
                mockMvc.perform(post("/api/v1/auth/logout")
                                .header("Authorization", "Bearer test-token"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.message").value("Logged out successfully"))
                                .andExpect(jsonPath("$.status").value("success"));

                verify(authenticationService, times(1)).logout(anyString());
        }


        @Test
        void testHealth() throws Exception {
                // Act & Assert
                mockMvc.perform(get("/api/v1/auth/health"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value("UP"))
                                .andExpect(jsonPath("$.service").value("authentication-service"))
                                .andExpect(jsonPath("$.version").value("2.0"));
        }

        @Test
        void testInfo() throws Exception {
                // Act & Assert
                mockMvc.perform(get("/api/v1/auth/info"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.service").value("authentication-service"))
                                .andExpect(jsonPath("$.version").value("2.0"))
                                .andExpect(jsonPath("$.type").value("stateless-jwt"))
                                .andExpect(jsonPath("$.features").isArray());
        }
}
