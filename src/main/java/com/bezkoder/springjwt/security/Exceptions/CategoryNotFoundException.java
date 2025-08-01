package com.bezkoder.springjwt.security.Exceptions;

public class CategoryNotFoundException extends RuntimeException     {
    public CategoryNotFoundException(String message) {
        super(message);
    }
}
