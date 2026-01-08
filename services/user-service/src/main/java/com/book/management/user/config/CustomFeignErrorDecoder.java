package com.book.management.user.config;

import com.book.management.user.exception.UserNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Custom Error Decoder to handle downstream exceptions from Order and Review services.
 * It parses the 'ErrorResponse' structure used by those services.
 * 
 * @author Aditya Srivastava
 * @version 1.0
 * @since 2026-01-08
 */
@Slf4j
public class CustomFeignErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper;

    public CustomFeignErrorDecoder() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule()); // For LocalDateTime parsing
    }

    @Override
    public Exception decode(String methodKey, Response response) {
        String downstreamMessage = "Service communication failure";

        try (InputStream inputStream = response.body() != null ? response.body().asInputStream() : null) {
            if (inputStream != null) {
                Map<String, Object> body = objectMapper.readValue(inputStream, Map.class);
                downstreamMessage = body.getOrDefault("message", downstreamMessage).toString();
            }
        } catch (IOException e) {
            log.warn("Could not parse error body from downstream service for {}", methodKey);
        }

        log.error("Downstream Error [Status {}] at {}: {}", response.status(), methodKey, downstreamMessage);

        return switch (response.status()) {
            case 404 -> new UserNotFoundException("Downstream service issue: " + downstreamMessage);
            default -> new RuntimeException("User service operation failed due to: " + downstreamMessage);
        };
    }
}
