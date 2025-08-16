package com.bezkoder.springjwt.security.Exceptions;

public class OrderCreateException extends RuntimeException {
    public OrderCreateException(String message) {
        super(message);
    }
}
