package com.book.management.authentication.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// ==========================================
// TOKEN VALIDATION REQUEST DTO
// ==========================================
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenValidationRequest {

    @NotBlank(message = "Token is required")
    private String token;
}
