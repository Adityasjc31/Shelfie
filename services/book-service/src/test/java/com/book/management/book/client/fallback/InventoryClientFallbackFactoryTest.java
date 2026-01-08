package com.book.management.book.client.fallback;

import com.book.management.book.client.InventoryClient;
import com.book.management.book.dto.requestdto.InventoryCreateDTO;
import com.book.management.book.dto.responsedto.InventoryResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("InventoryClientFallbackFactory Tests")
class InventoryClientFallbackFactoryTest {

    private InventoryClientFallbackFactory fallbackFactory;

    @BeforeEach
    void setUp() {
        fallbackFactory = new InventoryClientFallbackFactory();
    }

    @Nested
    @DisplayName("Create Fallback Tests")
    class CreateFallbackTests {

        @Test
        @DisplayName("Should create fallback client")
        void shouldCreateFallbackClient() {
            Throwable cause = new RuntimeException("Service unavailable");

            InventoryClient fallbackClient = fallbackFactory.create(cause);

            assertNotNull(fallbackClient);
        }

        @Test
        @DisplayName("Should create different instances for different causes")
        void shouldCreateDifferentInstancesForDifferentCauses() {
            InventoryClient client1 = fallbackFactory.create(new RuntimeException("Error 1"));
            InventoryClient client2 = fallbackFactory.create(new RuntimeException("Error 2"));

            assertNotSame(client1, client2);
        }
    }

    @Nested
    @DisplayName("CreateInventory Fallback Tests")
    class CreateInventoryFallbackTests {

        @Test
        @DisplayName("Should return fallback response for createInventory")
        void shouldReturnFallbackResponseForCreateInventory() {
            InventoryClient fallbackClient = fallbackFactory.create(new RuntimeException("Connection refused"));
            
            InventoryCreateDTO request = InventoryCreateDTO.builder()
                .bookId(1L)
                .quantity(10)
                .lowStockThreshold(5)
                .build();

            InventoryResponseDTO response = fallbackClient.createInventory(request);

            assertNotNull(response);
            assertEquals(1L, response.getBookId());
            assertEquals(0, response.getQuantity());
            assertEquals(5, response.getLowStockThreshold());
            assertTrue(response.isLowStock());
            assertTrue(response.isOutOfStock());
            assertNotNull(response.getCreatedAt());
        }

        @Test
        @DisplayName("Should log authentication failure for 401 error")
        void shouldLogAuthenticationFailureFor401Error() {
            InventoryClient fallbackClient = fallbackFactory.create(new RuntimeException("401 Unauthorized"));
            
            InventoryCreateDTO request = InventoryCreateDTO.builder()
                .bookId(1L)
                .quantity(10)
                .lowStockThreshold(5)
                .build();

            InventoryResponseDTO response = fallbackClient.createInventory(request);

            assertNotNull(response);
        }

        @Test
        @DisplayName("Should log permission denied for 403 error")
        void shouldLogPermissionDeniedFor403Error() {
            InventoryClient fallbackClient = fallbackFactory.create(new RuntimeException("403 Forbidden"));
            
            InventoryCreateDTO request = InventoryCreateDTO.builder()
                .bookId(2L)
                .quantity(5)
                .lowStockThreshold(3)
                .build();

            InventoryResponseDTO response = fallbackClient.createInventory(request);

            assertNotNull(response);
            assertEquals(2L, response.getBookId());
        }
    }

    @Nested
    @DisplayName("GetInventoryByBookId Fallback Tests")
    class GetInventoryByBookIdFallbackTests {

        @Test
        @DisplayName("Should return fallback response for getInventoryByBookId")
        void shouldReturnFallbackResponseForGetInventoryByBookId() {
            InventoryClient fallbackClient = fallbackFactory.create(new RuntimeException("Service unavailable"));

            InventoryResponseDTO response = fallbackClient.getInventoryByBookId(100L);

            assertNotNull(response);
            assertEquals(100L, response.getBookId());
            assertEquals(0, response.getQuantity());
            assertEquals(5, response.getLowStockThreshold());
            assertTrue(response.isLowStock());
            assertTrue(response.isOutOfStock());
            assertNotNull(response.getUpdatedAt());
        }

        @Test
        @DisplayName("Should handle null message in cause")
        void shouldHandleNullMessageInCause() {
            InventoryClient fallbackClient = fallbackFactory.create(new RuntimeException());

            InventoryResponseDTO response = fallbackClient.getInventoryByBookId(200L);

            assertNotNull(response);
            assertEquals(200L, response.getBookId());
        }

        @Test
        @DisplayName("Should log 401 error for getInventoryByBookId")
        void shouldLog401ErrorForGetInventoryByBookId() {
            InventoryClient fallbackClient = fallbackFactory.create(new RuntimeException("401 error occurred"));

            InventoryResponseDTO response = fallbackClient.getInventoryByBookId(300L);

            assertNotNull(response);
            assertEquals(300L, response.getBookId());
        }
    }

    @Nested
    @DisplayName("DeleteInventoryByBookId Fallback Tests")
    class DeleteInventoryByBookIdFallbackTests {

        @Test
        @DisplayName("Should not throw exception for deleteInventoryByBookId")
        void shouldNotThrowExceptionForDeleteInventoryByBookId() {
            InventoryClient fallbackClient = fallbackFactory.create(new RuntimeException("Network error"));

            assertDoesNotThrow(() -> fallbackClient.deleteInventoryByBookId(1L));
        }

        @Test
        @DisplayName("Should log 401 error for delete operation")
        void shouldLog401ErrorForDeleteOperation() {
            InventoryClient fallbackClient = fallbackFactory.create(new RuntimeException("401 Unauthorized access"));

            assertDoesNotThrow(() -> fallbackClient.deleteInventoryByBookId(2L));
        }

        @Test
        @DisplayName("Should log 403 error for delete operation")
        void shouldLog403ErrorForDeleteOperation() {
            InventoryClient fallbackClient = fallbackFactory.create(new RuntimeException("403 Forbidden access"));

            assertDoesNotThrow(() -> fallbackClient.deleteInventoryByBookId(3L));
        }

        @Test
        @DisplayName("Should handle null cause message for delete operation")
        void shouldHandleNullCauseMessageForDeleteOperation() {
            InventoryClient fallbackClient = fallbackFactory.create(new RuntimeException());

            assertDoesNotThrow(() -> fallbackClient.deleteInventoryByBookId(4L));
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCasesTests {

        @Test
        @DisplayName("Should handle cause with empty message")
        void shouldHandleCauseWithEmptyMessage() {
            InventoryClient fallbackClient = fallbackFactory.create(new RuntimeException(""));

            InventoryResponseDTO response = fallbackClient.getInventoryByBookId(500L);

            assertNotNull(response);
        }

        @Test
        @DisplayName("Should handle NullPointerException as cause")
        void shouldHandleNullPointerExceptionAsCause() {
            InventoryClient fallbackClient = fallbackFactory.create(new NullPointerException("null reference"));

            InventoryResponseDTO response = fallbackClient.getInventoryByBookId(600L);

            assertNotNull(response);
            assertEquals(600L, response.getBookId());
        }

        @Test
        @DisplayName("Should handle IllegalStateException as cause")
        void shouldHandleIllegalStateExceptionAsCause() {
            InventoryClient fallbackClient = fallbackFactory.create(new IllegalStateException("Invalid state"));

            assertDoesNotThrow(() -> fallbackClient.deleteInventoryByBookId(700L));
        }
    }
}
