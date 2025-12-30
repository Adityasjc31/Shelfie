package com.book.management.api_gateway.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.webflux.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Global Exception Handler for API Gateway.
 * 
 * Provides centralized exception handling and custom error responses
 * for all gateway errors.
 * 
 * @author Aditya Srivastava
 * @version 1.0
 * @since 2024-12-29
 */
@Component
@Slf4j
public class GlobalExceptionHandler extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, 
                                                  ErrorAttributeOptions options) {
        Map<String, Object> errorAttributes = super.getErrorAttributes(request, options);
        Throwable error = getError(request);
        
        log.error("╔════════════════════════════════════════════════════════════");
        log.error("║ GATEWAY ERROR");
        log.error("║ Path: {}", request.path());
        log.error("║ Method: {}", request.method());
        log.error("║ Error Type: {}", error.getClass().getSimpleName());
        log.error("║ Message: {}", error.getMessage());
        log.error("╚════════════════════════════════════════════════════════════");
        
        // Customize error response
        HttpStatus status = determineHttpStatus(error);
        
        errorAttributes.put("timestamp", LocalDateTime.now().toString());
        errorAttributes.put("path", request.path());
        errorAttributes.put("status", status.value());
        errorAttributes.put("error", status.getReasonPhrase());
        errorAttributes.put("message", determineErrorMessage(error));
        
        // Remove trace from response (security)
        errorAttributes.remove("trace");
        
        return errorAttributes;
    }

    /**
     * Determines HTTP status based on exception type.
     */
    private HttpStatus determineHttpStatus(Throwable error) {
        if (error instanceof ResponseStatusException) {
            return HttpStatus.valueOf(((ResponseStatusException) error).getStatusCode().value());
        } else if (error instanceof UnauthorizedException) {
            return HttpStatus.UNAUTHORIZED;
        } else if (error instanceof ServiceUnavailableException) {
            return HttpStatus.SERVICE_UNAVAILABLE;
        } else if (error instanceof TimeoutException) {
            return HttpStatus.GATEWAY_TIMEOUT;
        }
        
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    /**
     * Determines user-friendly error message.
     */
    private String determineErrorMessage(Throwable error) {
        if (error instanceof UnauthorizedException) {
            return "Authentication failed. Please check your credentials.";
        } else if (error instanceof ServiceUnavailableException) {
            return "The requested service is temporarily unavailable. Please try again later.";
        } else if (error instanceof TimeoutException) {
            return "Request timeout. The service took too long to respond.";
        }
        
        // Don't expose internal error details to clients
        return "An error occurred while processing your request.";
    }
}

