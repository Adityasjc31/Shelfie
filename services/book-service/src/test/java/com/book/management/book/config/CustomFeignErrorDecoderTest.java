package com.book.management.book.config;

import com.book.management.book.exception.BookNotFoundException;
import com.book.management.book.exception.DuplicateBookException;
import com.book.management.book.exception.InvalidBookDataException;
import feign.Request;
import feign.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CustomFeignErrorDecoder Tests")
class CustomFeignErrorDecoderTest {

    private CustomFeignErrorDecoder errorDecoder;

    @BeforeEach
    void setUp() {
        errorDecoder = new CustomFeignErrorDecoder();
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create error decoder successfully")
        void shouldCreateErrorDecoderSuccessfully() {
            CustomFeignErrorDecoder decoder = new CustomFeignErrorDecoder();
            assertNotNull(decoder);
        }
    }

    @Nested
    @DisplayName("Decode - Status 400 (Bad Request)")
    class DecodeBadRequestTests {

        @Test
        @DisplayName("Should return InvalidBookDataException for 400 status")
        void shouldReturnInvalidBookDataExceptionFor400Status() {
            Response response = createResponse(400, "{\"message\": \"Invalid book data\"}");

            Exception result = errorDecoder.decode("InventoryClient#createInventory", response);

            assertInstanceOf(InvalidBookDataException.class, result);
            assertTrue(result.getMessage().contains("Inventory Request Issue"));
            assertTrue(result.getMessage().contains("Invalid book data"));
        }

        @Test
        @DisplayName("Should handle 400 status with null body")
        void shouldHandle400StatusWithNullBody() {
            Response response = createResponseWithNullBody(400);

            Exception result = errorDecoder.decode("InventoryClient#createInventory", response);

            assertInstanceOf(InvalidBookDataException.class, result);
            assertTrue(result.getMessage().contains("Service communication failure"));
        }
    }

    @Nested
    @DisplayName("Decode - Status 404 (Not Found)")
    class DecodeNotFoundTests {

        @Test
        @DisplayName("Should return BookNotFoundException for 404 status")
        void shouldReturnBookNotFoundExceptionFor404Status() {
            Response response = createResponse(404, "{\"message\": \"Book not found\"}");

            Exception result = errorDecoder.decode("InventoryClient#getInventory", response);

            assertInstanceOf(BookNotFoundException.class, result);
            assertTrue(result.getMessage().contains("Inventory record not found"));
            assertTrue(result.getMessage().contains("Book not found"));
        }

        @Test
        @DisplayName("Should handle 404 status with default message")
        void shouldHandle404StatusWithDefaultMessage() {
            Response response = createResponse(404, "{}");

            Exception result = errorDecoder.decode("InventoryClient#getInventory", response);

            assertInstanceOf(BookNotFoundException.class, result);
        }
    }

    @Nested
    @DisplayName("Decode - Status 409 (Conflict)")
    class DecodeConflictTests {

        @Test
        @DisplayName("Should return DuplicateBookException for 409 status")
        void shouldReturnDuplicateBookExceptionFor409Status() {
            Request request = Request.create(Request.HttpMethod.POST, 
                "http://inventory-service/api/inventory/106",
                Collections.emptyMap(), null, StandardCharsets.UTF_8, null);
            Response response = createResponseWithRequest(409, "{\"message\": \"Duplicate entry\"}", request);

            Exception result = errorDecoder.decode("InventoryClient#createInventory", response);

            assertInstanceOf(DuplicateBookException.class, result);
        }

        @Test
        @DisplayName("Should extract ID from URL for 409 status")
        void shouldExtractIdFromUrlFor409Status() {
            Request request = Request.create(Request.HttpMethod.POST,
                "http://inventory-service/api/inventory/123",
                Collections.emptyMap(), null, StandardCharsets.UTF_8, null);
            Response response = createResponseWithRequest(409, "{}", request);

            Exception result = errorDecoder.decode("InventoryClient#createInventory", response);

            assertInstanceOf(DuplicateBookException.class, result);
        }

        @Test
        @DisplayName("Should return 0 when ID extraction fails")
        void shouldReturnZeroWhenIdExtractionFails() {
            Request request = Request.create(Request.HttpMethod.POST,
                "http://inventory-service/api/inventory/invalid",
                Collections.emptyMap(), null, StandardCharsets.UTF_8, null);
            Response response = createResponseWithRequest(409, "{}", request);

            Exception result = errorDecoder.decode("InventoryClient#createInventory", response);

            assertInstanceOf(DuplicateBookException.class, result);
        }
    }

    @Nested
    @DisplayName("Decode - Default Status")
    class DecodeDefaultTests {

        @Test
        @DisplayName("Should return InvalidBookDataException for 500 status")
        void shouldReturnInvalidBookDataExceptionFor500Status() {
            Response response = createResponse(500, "{\"message\": \"Internal error\"}");

            Exception result = errorDecoder.decode("InventoryClient#getInventory", response);

            assertInstanceOf(InvalidBookDataException.class, result);
            assertTrue(result.getMessage().contains("Book-Inventory synchronization failed"));
        }

        @Test
        @DisplayName("Should return InvalidBookDataException for 503 status")
        void shouldReturnInvalidBookDataExceptionFor503Status() {
            Response response = createResponse(503, "{\"message\": \"Service unavailable\"}");

            Exception result = errorDecoder.decode("InventoryClient#getInventory", response);

            assertInstanceOf(InvalidBookDataException.class, result);
        }
    }

    @Nested
    @DisplayName("Error Body Parsing")
    class ErrorBodyParsingTests {

        @Test
        @DisplayName("Should handle malformed JSON body")
        void shouldHandleMalformedJsonBody() {
            Response response = createResponse(400, "not valid json");

            Exception result = errorDecoder.decode("InventoryClient#createInventory", response);

            assertInstanceOf(InvalidBookDataException.class, result);
            assertTrue(result.getMessage().contains("Service communication failure"));
        }

        @Test
        @DisplayName("Should handle empty body")
        void shouldHandleEmptyBody() {
            Response response = createResponse(400, "");

            Exception result = errorDecoder.decode("InventoryClient#createInventory", response);

            assertInstanceOf(InvalidBookDataException.class, result);
        }
    }

    // Helper methods
    private Response createResponse(int status, String body) {
        Request request = Request.create(Request.HttpMethod.GET,
            "http://inventory-service/api/inventory/1",
            Collections.emptyMap(), null, StandardCharsets.UTF_8, null);
        
        return Response.builder()
            .status(status)
            .reason("Test")
            .headers(Collections.emptyMap())
            .request(request)
            .body(body, StandardCharsets.UTF_8)
            .build();
    }

    private Response createResponseWithRequest(int status, String body, Request request) {
        return Response.builder()
            .status(status)
            .reason("Test")
            .headers(Collections.emptyMap())
            .request(request)
            .body(body, StandardCharsets.UTF_8)
            .build();
    }

    private Response createResponseWithNullBody(int status) {
        Request request = Request.create(Request.HttpMethod.GET,
            "http://inventory-service/api/inventory/1",
            Collections.emptyMap(), null, StandardCharsets.UTF_8, null);
        
        return Response.builder()
            .status(status)
            .reason("Test")
            .headers(Collections.emptyMap())
            .request(request)
            .body((byte[]) null)
            .build();
    }
}
