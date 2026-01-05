package com.book.management.book.config;

import com.book.management.book.exception.BookNotFoundException;
import com.book.management.book.exception.DuplicateBookException;
import com.book.management.book.exception.InvalidBookDataException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@Slf4j
public class CustomFeignErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper;

    public CustomFeignErrorDecoder() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
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
            case 400 ->
                    new InvalidBookDataException("Inventory Request Issue: " + downstreamMessage);

            case 404 ->
                    new BookNotFoundException("Inventory record not found for this book: " + downstreamMessage);

            case 409 ->
                // Extract ID from URL (e.g., .../inventory/106) to satisfy the 'int' requirement
                    new DuplicateBookException(extractId(response));

            default ->
                    new InvalidBookDataException("Book-Inventory synchronization failed: " + downstreamMessage);
        };
    }

    // Simple helper to grab the ID from the end of the URL
    private int extractId(Response response) {
        try {
            String url = response.request().url();
            return Integer.parseInt(url.substring(url.lastIndexOf("/") + 1));
        } catch (Exception e) {
            return 0;
        }
    }
}