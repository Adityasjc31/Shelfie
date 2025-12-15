package com.db.ms.Order.exception;

public class OrderInvalidStatusTransitionException extends RuntimeException {
    public OrderInvalidStatusTransitionException(String message) {
        super(message);
    }
}
