package com.db.ms.order.dto.responsedto;

/**
 * Data Transfer Object for standardized error responses, used by the
 * GlobalOrderExceptionHandler.
 */
public class ErrorResponseDTO {

    private final int status;
    private final String message;
    private final String details;

    /**
     * @param status The HTTP status code (e.g., 400, 404, 500).
     * @param message A short, descriptive error message (usually the exception message).
     * @param details Contextual details about the request or error origin (e.g., request URI).
     */
    public ErrorResponseDTO(int status, String message, String details) {
        this.status = status;
        this.message = message;
        this.details = details;
    }

    // Getters
    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getDetails() {
        return details;
    }

    // You would typically add Lombok annotations like @Getter and @AllArgsConstructor
    // or generate toString(), equals(), and hashCode() here.
}