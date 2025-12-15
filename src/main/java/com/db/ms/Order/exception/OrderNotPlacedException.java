package com.db.ms.Order.exception;

public class OrderNotPlacedException extends RuntimeException {

    public OrderNotPlacedException(String message) {
        super(message);
    }
}
