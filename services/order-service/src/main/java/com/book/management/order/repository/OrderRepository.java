package com.book.management.order.repository;

import com.book.management.order.enums.OrderEnum;
import com.book.management.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository interface for Order entity.
 * Backed by MySQL via Spring Data JPA.
 * * @author Rehan Ashraf
 * @version 2.0
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Finds orders based on their current status.
     * Note: Hibernate @SQLRestriction handles the isDeleted filter automatically.
     */
    List<Order> findByOrderStatus(OrderEnum orderStatus);
}