package com.db.ms.exception;

public class OrderInvalidStatusTransitionException extends RuntimeException {
    public OrderInvalidStatusTransitionException(String message) {
        super(message);
    }
}
