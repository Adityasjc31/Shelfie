package com.db.ms.order.exception;

public class OrderNotPlacedException extends RuntimeException {

    public OrderNotPlacedException(String message) {
        super(message);
    }
}
