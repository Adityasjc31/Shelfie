package com.book.management.order.config;

import com.book.management.order.exception.OrderNotPlacedException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Custom Error Decoder to handle downstream exceptions from Book and Inventory services.
 * It parses the 'ErrorResponse' structure used by those services.
 */
@Slf4j
@Configuration
public class CustomFeignErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper;

    public CustomFeignErrorDecoder() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule()); // For LocalDateTime parsing
    }

    @Override
    public Exception decode(String methodKey, Response response) {
        String downstreamMessage = "Service communication failure";

        // Extracting the 'message' field from the downstream ErrorResponse JSON
        try (InputStream inputStream = response.body().asInputStream()) {
            Map<String, Object> body = objectMapper.readValue(inputStream, Map.class);
            downstreamMessage = body.getOrDefault("message", downstreamMessage).toString();
        } catch (IOException | NullPointerException e) {
            log.warn("Could not parse error body from downstream service for {}", methodKey);
        }

        log.error("Downstream Error [Status {}]: {}", response.status(), downstreamMessage);

        return switch (response.status()) {
            case 400 -> new OrderNotPlacedException("Inventory Issue: " + downstreamMessage);
            case 404 -> new OrderNotPlacedException("Catalog Issue: " + downstreamMessage);
            default -> new OrderNotPlacedException("Order process failed due to: " + downstreamMessage);
        };
    }
}