// ============================================================================
// FILE: src/test/java/com/bookstore/user/controller/UserControllerTest.java
// ============================================================================
package com.book.management.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.book.management.user.dto.*;
import com.book.management.user.filter.GatewayAuthenticationFilter;
import com.book.management.user.model.UserRole;
import com.book.management.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for UserController class.
 * Tests all REST API endpoints with various scenarios.
 * 
 * Uses MockMvc for testing REST endpoints and Mockito for service mocking.
 * 
 * @author Digital Bookstore Team
 * @version 1.0
 */
@WebMvcTest(value = UserController.class,
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE,
                classes = GatewayAuthenticationFilter.class))
@DisplayName("UserController Unit Tests")
class UserControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockitoBean
        private UserService userService;

        private UserResponseDTO userResponseDTO;
        private UserRegistrationDTO registrationDTO;
        private UserLoginDTO loginDTO;
        private UserUpdateDTO updateDTO;

        @BeforeEach
        void setUp() {
                userResponseDTO = UserResponseDTO.builder()
                                .userId(1L)
                                .name("John Doe")
                                .email("john@example.com")
                                .role("CUSTOMER")
                                .isActive(true)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();

                registrationDTO = UserRegistrationDTO.builder()
                                .name("John Doe")
                                .email("john@example.com")
                                .password("password123")
                                .role("CUSTOMER")
                                .build();

                loginDTO = UserLoginDTO.builder()
                                .email("john@example.com")
                                .password("password123")
                                .build();

                updateDTO = UserUpdateDTO.builder()
                                .name("John Updated")
                                .password("newPassword123")
                                .build();
        }

        // ========== REGISTRATION ENDPOINT TESTS ==========

        @Test
        @DisplayName("POST /api/v1/users/register - Should register user successfully")
        void testRegisterUser_Success() throws Exception {
                when(userService.registerUser(any(UserRegistrationDTO.class)))
                                .thenReturn(userResponseDTO);

                mockMvc.perform(post("/api/v1/users/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(registrationDTO)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.userId", is(1)))
                                .andExpect(jsonPath("$.email", is("john@example.com")))
                                .andExpect(jsonPath("$.name", is("John Doe")))
                                .andExpect(jsonPath("$.role", is("CUSTOMER")))
                                .andExpect(jsonPath("$.isActive", is(true)));

                verify(userService, times(1)).registerUser(any(UserRegistrationDTO.class));
        }

        @Test
        @DisplayName("POST /api/v1/users/register - Should return 400 for invalid input")
        void testRegisterUser_InvalidInput() throws Exception {
                UserRegistrationDTO invalidDTO = UserRegistrationDTO.builder()
                                .name("")
                                .email("invalid-email")
                                .password("123")
                                .build();

                mockMvc.perform(post("/api/v1/users/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(invalidDTO)))
                                .andExpect(status().isBadRequest());

                verify(userService, never()).registerUser(any(UserRegistrationDTO.class));
        }

        // ========== LOGIN ENDPOINT TESTS ==========

        @Test
        @DisplayName("POST /api/v1/users/login - Should login user successfully")
        void testLoginUser_Success() throws Exception {
                when(userService.loginUser(any(UserLoginDTO.class)))
                                .thenReturn(userResponseDTO);

                mockMvc.perform(post("/api/v1/users/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginDTO)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.userId", is(1)))
                                .andExpect(jsonPath("$.email", is("john@example.com")));

                verify(userService, times(1)).loginUser(any(UserLoginDTO.class));
        }

        // ========== GET USER BY ID ENDPOINT TESTS ==========

        @Test
        @DisplayName("GET /api/v1/users/{userId} - Should get user by ID successfully")
        void testGetUserById_Success() throws Exception {
                when(userService.getUserById(1L)).thenReturn(userResponseDTO);

                mockMvc.perform(get("/api/v1/users/1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.userId", is(1)))
                                .andExpect(jsonPath("$.email", is("john@example.com")))
                                .andExpect(jsonPath("$.name", is("John Doe")));

                verify(userService, times(1)).getUserById(1L);
        }

        // ========== GET USER BY EMAIL ENDPOINT TESTS ==========

        @Test
        @DisplayName("GET /api/v1/users/email/{email} - Should get user by email successfully")
        void testGetUserByEmail_Success() throws Exception {
                when(userService.getUserByEmail("john@example.com"))
                                .thenReturn(userResponseDTO);

                mockMvc.perform(get("/api/v1/users/email/john@example.com"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.email", is("john@example.com")));

                verify(userService, times(1)).getUserByEmail("john@example.com");
        }

        // ========== GET ALL USERS ENDPOINT TESTS ==========

        @Test
        @DisplayName("GET /api/v1/users - Should get all users successfully")
        void testGetAllUsers_Success() throws Exception {
                UserResponseDTO user2 = UserResponseDTO.builder()
                                .userId(2L)
                                .name("Jane Doe")
                                .email("jane@example.com")
                                .role("ADMIN")
                                .isActive(true)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();

                List<UserResponseDTO> users = Arrays.asList(userResponseDTO, user2);
                when(userService.getAllUsers()).thenReturn(users);

                mockMvc.perform(get("/api/v1/users"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(2)))
                                .andExpect(jsonPath("$[0].email", is("john@example.com")))
                                .andExpect(jsonPath("$[1].email", is("jane@example.com")));

                verify(userService, times(1)).getAllUsers();
        }

        // ========== GET USERS BY ROLE ENDPOINT TESTS ==========

        @Test
        @DisplayName("GET /api/v1/users/role/{role} - Should get users by role successfully")
        void testGetUsersByRole_Success() throws Exception {
                List<UserResponseDTO> customers = Arrays.asList(userResponseDTO);
                when(userService.getUsersByRole(UserRole.CUSTOMER)).thenReturn(customers);

                mockMvc.perform(get("/api/v1/users/role/CUSTOMER"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$", hasSize(1)))
                                .andExpect(jsonPath("$[0].role", is("CUSTOMER")));

                verify(userService, times(1)).getUsersByRole(UserRole.CUSTOMER);
        }

        // ========== UPDATE USER PROFILE ENDPOINT TESTS ==========

        @Test
        @DisplayName("PUT /api/v1/users/{userId} - Should update user profile successfully")
        void testUpdateUserProfile_Success() throws Exception {
                UserResponseDTO updatedUser = UserResponseDTO.builder()
                                .userId(1L)
                                .name("John Updated")
                                .email("john@example.com")
                                .role("CUSTOMER")
                                .isActive(true)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();

                when(userService.updateUserProfile(anyLong(), any(UserUpdateDTO.class)))
                                .thenReturn(updatedUser);

                mockMvc.perform(put("/api/v1/users/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateDTO)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.name", is("John Updated")));

                verify(userService, times(1))
                                .updateUserProfile(anyLong(), any(UserUpdateDTO.class));
        }

        // ========== DEACTIVATE USER ENDPOINT TESTS ==========

        @Test
        @DisplayName("PATCH /api/v1/users/{userId}/deactivate - Should deactivate user successfully")
        void testDeactivateUser_Success() throws Exception {
                doNothing().when(userService).deactivateUser(1L);

                mockMvc.perform(patch("/api/v1/users/1/deactivate"))
                                .andExpect(status().isNoContent());

                verify(userService, times(1)).deactivateUser(1L);
        }

        // ========== REACTIVATE USER ENDPOINT TESTS ==========

        @Test
        @DisplayName("PATCH /api/v1/users/{userId}/reactivate - Should reactivate user successfully")
        void testReactivateUser_Success() throws Exception {
                doNothing().when(userService).reactivateUser(1L);

                mockMvc.perform(patch("/api/v1/users/1/reactivate"))
                                .andExpect(status().isNoContent());

                verify(userService, times(1)).reactivateUser(1L);
        }

        // ========== DELETE USER ENDPOINT TESTS ==========

        @Test
        @DisplayName("DELETE /api/v1/users/{userId} - Should delete user successfully")
        void testDeleteUser_Success() throws Exception {
                doNothing().when(userService).deleteUser(1L);

                mockMvc.perform(delete("/api/v1/users/1"))
                                .andExpect(status().isNoContent());

                verify(userService, times(1)).deleteUser(1L);
        }
}

