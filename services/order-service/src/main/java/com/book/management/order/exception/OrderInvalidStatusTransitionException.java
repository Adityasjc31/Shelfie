package com.book.management.order.exception;

public class OrderInvalidStatusTransitionException extends RuntimeException {
    public OrderInvalidStatusTransitionException(String message) {
        super(message);
    }
}
