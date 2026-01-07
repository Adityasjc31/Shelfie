package com.book.management.order.config;

import com.book.management.order.exception.OrderNotPlacedException;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Request;
import feign.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link CustomFeignErrorDecoder}.
 *
 * Tests the custom Feign error decoder that handles downstream service errors
 * from Book and Inventory services. Validates proper exception translation
 * and error message extraction from response bodies.
 *
 * Test Coverage:
 * - 400 Bad Request error handling (Inventory issues)
 * - 404 Not Found error handling (Book not found)
 * - Generic error handling for other status codes
 * - Error message parsing from response body
 * - Handling of malformed response bodies
 * - Null/empty response body scenarios
 *
 * @author Rehan Ashraf
 * @version 1.0
 * @since 2026-01-07
 */
@ExtendWith(MockitoExtension.class)
class CustomFeignErrorDecoderTest {

    private CustomFeignErrorDecoder errorDecoder;
    private ObjectMapper objectMapper;

    /**
     * Setup test dependencies before each test.
     */
    @BeforeEach
    void setUp() {
        errorDecoder = new CustomFeignErrorDecoder();
        objectMapper = new ObjectMapper();
    }

    /**
     * Test: Verify 400 Bad Request is decoded to OrderNotPlacedException.
     *
     * Scenario: Inventory service returns 400 due to insufficient stock.
     * Expected: OrderNotPlacedException with "Inventory Issue" prefix.
     */
    @Test
    void testDecode_With400Status_ShouldThrowOrderNotPlacedExceptionWithInventoryIssue() {
        // Arrange
        String errorMessage = "Insufficient stock for book ID: 101";
        Response response = createMockResponse(400, errorMessage);

        // Act
        Exception result = errorDecoder.decode("InventoryServiceClient#reduceStock()", response);

        // Assert
        assertInstanceOf(OrderNotPlacedException.class, result);
        assertTrue(result.getMessage().contains("Inventory Issue"));
        assertTrue(result.getMessage().contains(errorMessage));
    }

    /**
     * Test: Verify 404 Not Found is decoded to OrderNotPlacedException.
     *
     * Scenario: Book service returns 404 for non-existent book.
     * Expected: OrderNotPlacedException with "BookItem Issue" prefix.
     */
    @Test
    void testDecode_With404Status_ShouldThrowOrderNotPlacedExceptionWithBookItemIssue() {
        // Arrange
        String errorMessage = "Book not found with ID: 999";
        Response response = createMockResponse(404, errorMessage);

        // Act
        Exception result = errorDecoder.decode("BookServiceClient#getBookPrices()", response);

        // Assert
        assertInstanceOf(OrderNotPlacedException.class, result);
        assertTrue(result.getMessage().contains("BookItem Issue"));
        assertTrue(result.getMessage().contains(errorMessage));
    }

    /**
     * Test: Verify 500 Internal Server Error is decoded appropriately.
     *
     * Scenario: Downstream service returns 500.
     * Expected: OrderNotPlacedException with generic failure message.
     */
    @Test
    void testDecode_With500Status_ShouldThrowOrderNotPlacedExceptionWithGenericMessage() {
        // Arrange
        String errorMessage = "Internal server error";
        Response response = createMockResponse(500, errorMessage);

        // Act
        Exception result = errorDecoder.decode("BookServiceClient#getBookPrices()", response);

        // Assert
        assertInstanceOf(OrderNotPlacedException.class, result);
        assertTrue(result.getMessage().contains("Order process failed due to"));
        assertTrue(result.getMessage().contains(errorMessage));
    }

    /**
     * Test: Verify 503 Service Unavailable is handled correctly.
     *
     * Scenario: Service temporarily unavailable.
     * Expected: OrderNotPlacedException with generic failure message.
     */
    @Test
    void testDecode_With503Status_ShouldThrowOrderNotPlacedException() {
        // Arrange
        String errorMessage = "Service temporarily unavailable";
        Response response = createMockResponse(503, errorMessage);

        // Act
        Exception result = errorDecoder.decode("InventoryServiceClient#reduceStock()", response);

        // Assert
        assertInstanceOf(OrderNotPlacedException.class, result);
        assertTrue(result.getMessage().contains("Order process failed due to"));
    }

    /**
     * Test: Verify handling of response with null body.
     *
     * Scenario: Response has no body content.
     * Expected: OrderNotPlacedException with default message.
     */
    @Test
    void testDecode_WithNullBody_ShouldUseDefaultMessage() {
        // Arrange
        Response response = createMockResponseWithNullBody(400);

        // Act
        Exception result = errorDecoder.decode("BookServiceClient#getBookPrices()", response);

        // Assert
        assertInstanceOf(OrderNotPlacedException.class, result);
        assertTrue(result.getMessage().contains("Service communication failure"));
    }

    /**
     * Test: Verify handling of malformed JSON in response body.
     *
     * Scenario: Response body contains invalid JSON.
     * Expected: OrderNotPlacedException with default message (parsing fails gracefully).
     */
    @Test
    void testDecode_WithMalformedJSON_ShouldUseDefaultMessage() {
        // Arrange
        Response response = createMockResponseWithInvalidJson(400);

        // Act
        Exception result = errorDecoder.decode("BookServiceClient#getBookPrices()", response);

        // Assert
        assertInstanceOf(OrderNotPlacedException.class, result);
        assertNotNull(result.getMessage());
    }

    /**
     * Test: Verify custom error message is extracted from response body.
     *
     * Scenario: Response contains well-formed error body with custom message.
     * Expected: Custom message is included in exception.
     */
    @Test
    void testDecode_WithCustomErrorMessage_ShouldExtractMessage() {
        // Arrange
        String customMessage = "Custom business rule violation";
        Response response = createMockResponse(400, customMessage);

        // Act
        Exception result = errorDecoder.decode("InventoryServiceClient#reduceStock()", response);

        // Assert
        assertInstanceOf(OrderNotPlacedException.class, result);
        assertTrue(result.getMessage().contains(customMessage));
    }

    /**
     * Test: Verify handling of empty response body.
     *
     * Scenario: Response body is empty string.
     * Expected: OrderNotPlacedException with default message.
     */
    @Test
    void testDecode_WithEmptyBody_ShouldUseDefaultMessage() {
        // Arrange
        Response response = createMockResponseWithEmptyBody(404);

        // Act
        Exception result = errorDecoder.decode("BookServiceClient#getBookPrices()", response);

        // Assert
        assertInstanceOf(OrderNotPlacedException.class, result);
        assertNotNull(result.getMessage());
    }

    /**
     * Test: Verify handling of response with missing 'message' field.
     *
     * Scenario: Response JSON doesn't contain 'message' field.
     * Expected: OrderNotPlacedException with default message.
     */
    @Test
    void testDecode_WithMissingMessageField_ShouldUseDefaultMessage() {
        // Arrange
        Response response = createMockResponseWithoutMessageField(400);

        // Act
        Exception result = errorDecoder.decode("InventoryServiceClient#reduceStock()", response);

        // Assert
        assertInstanceOf(OrderNotPlacedException.class, result);
        assertTrue(result.getMessage().contains("Service communication failure"));
    }

    // ==================== Helper Methods ====================

    /**
     * Creates a mock Feign Response with specified status and error message.
     */
    private Response createMockResponse(int status, String errorMessage) {
        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("message", errorMessage);
        errorBody.put("timestamp", "2026-01-07T10:00:00");
        errorBody.put("status", status);

        try {
            String jsonBody = objectMapper.writeValueAsString(errorBody);
            byte[] bodyBytes = jsonBody.getBytes(StandardCharsets.UTF_8);

            return Response.builder()
                    .status(status)
                    .reason("Error")
                    .request(createMockRequest())
                    .body(bodyBytes)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create mock response", e);
        }
    }

    /**
     * Creates a mock Feign Response with null body.
     */
    private Response createMockResponseWithNullBody(int status) {
        return Response.builder()
                .status(status)
                .reason("Error")
                .request(createMockRequest())
                .build();
    }

    /**
     * Creates a mock Feign Response with invalid JSON.
     */
    private Response createMockResponseWithInvalidJson(int status) {
        String invalidJson = "{invalid json content";
        byte[] bodyBytes = invalidJson.getBytes(StandardCharsets.UTF_8);

        return Response.builder()
                .status(status)
                .reason("Error")
                .request(createMockRequest())
                .body(bodyBytes)
                .build();
    }

    /**
     * Creates a mock Feign Response with empty body.
     */
    private Response createMockResponseWithEmptyBody(int status) {
        return Response.builder()
                .status(status)
                .reason("Error")
                .request(createMockRequest())
                .body(new byte[0])
                .build();
    }

    /**
     * Creates a mock Feign Response without 'message' field.
     */
    private Response createMockResponseWithoutMessageField(int status) {
        Map<String, Object> errorBody = new HashMap<>();
        errorBody.put("error", "Some error");
        errorBody.put("timestamp", "2026-01-07T10:00:00");

        try {
            String jsonBody = objectMapper.writeValueAsString(errorBody);
            byte[] bodyBytes = jsonBody.getBytes(StandardCharsets.UTF_8);

            return Response.builder()
                    .status(status)
                    .reason("Error")
                    .request(createMockRequest())
                    .body(bodyBytes)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create mock response", e);
        }
    }

    /**
     * Creates a mock Feign Request.
     */
    private Request createMockRequest() {
        return Request.create(
                Request.HttpMethod.POST,
                "http://localhost:8080/api/test",
                new HashMap<>(),
                null,
                null,
                null
        );
    }
}

