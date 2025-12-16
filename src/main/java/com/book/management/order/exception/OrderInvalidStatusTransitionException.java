package com.db.ms.order.exception;

public class OrderInvalidStatusTransitionException extends RuntimeException {
    public OrderInvalidStatusTransitionException(String message) {
        super(message);
    }
}
