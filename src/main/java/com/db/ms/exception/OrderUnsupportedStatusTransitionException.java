package com.db.ms.exception;

public class OrderUnsupportedStatusTransitionException extends RuntimeException {
    public OrderUnsupportedStatusTransitionException(String message) {
        super(message);
    }
}
