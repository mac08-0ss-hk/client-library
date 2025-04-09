package com.processapi.rest.circuitbreaker;

public class CircuitBreakerOpenException extends RuntimeException {
    public CircuitBreakerOpenException(String message) {
        super(message);
    }

    public CircuitBreakerOpenException(String message, Throwable cause) {
        super(message, cause);
    }
} 