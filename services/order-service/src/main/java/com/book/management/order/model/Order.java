package com.book.management.order.model;

import com.book.management.order.enums.OrderEnum;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Entity class representing an Order in the Digital Book Store.
 * * Migrated to JPA for MySQL persistence.
 * Features:
 * - Soft Delete: Uses @SQLDelete to set isDeleted=true instead of physical deletion.
 * - ElementCollection: Stores order items (bookId and quantity) in a relational table.
 *
 * @author Rehan Ashraf
 * @version 2.0 (Microservice Migration)
 * @since 2024-12-15
 */
@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE orders SET is_deleted = true WHERE order_id = ?")
@SQLRestriction("is_deleted = false") // Ensures deleted records are excluded from queries
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @Column(nullable = false)
    private Long userId;

    /**
     * Map of Book IDs to their respective quantities.
     * Persisted in a separate 'order_items' table.
     */
    @ElementCollection
    @CollectionTable(name = "order_items", joinColumns = @JoinColumn(name = "order_id"))
    @MapKeyColumn(name = "book_id")
    @Column(name = "quantity")
    private Map<Long, Integer> items;

    @Column(nullable = false)
    private LocalDateTime orderDateTime;

    @Column(nullable = false)
    private double orderTotalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderEnum orderStatus;

    @Column(nullable = false)
    private boolean isDeleted = false; // Flag for soft deletion logic
}