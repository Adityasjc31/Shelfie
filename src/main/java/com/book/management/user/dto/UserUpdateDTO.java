// ============================================================================
// FILE: src/main/java/com/bookstore/user/dto/UserUpdateDTO.java
// ============================================================================
package com.book.management.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object for updating user profile.
 * All fields are optional.
 * 
 * @author Digital Bookstore Team
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserUpdateDTO {

    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @Size(min = 6, max = 50, message = "Password must be between 6 and 50 characters")
    private String password;
}