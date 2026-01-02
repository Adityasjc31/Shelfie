package com.book.management.order.dto.responsedto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standardized Error Response DTO for the Order Service.
 * Aggregates standard HTTP error details with specific validation metadata.
 * * @author Rehan Ashraf
 * @version 2.1
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDTO {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String details;
    private String path;

    /** * Map of field-specific validation errors.
     * Key: Field name (e.g., "userId", "bookId")
     * Value: Validation message (e.g., "must not be null")
     */
    private Map<String, String> validationErrors;
}