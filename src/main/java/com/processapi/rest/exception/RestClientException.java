package com.processapi.rest.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class RestClientException extends RuntimeException {
    private final String clientName;
    private final HttpStatus statusCode;
    private final String responseBody;
    private final String errorCode;

    public RestClientException(String clientName, String message) {
        super(message);
        this.clientName = clientName;
        this.statusCode = null;
        this.responseBody = null;
        this.errorCode = null;
    }

    public RestClientException(String clientName, String message, Throwable cause) {
        super(message, cause);
        this.clientName = clientName;
        this.statusCode = null;
        this.responseBody = null;
        this.errorCode = null;
    }

    public RestClientException(String clientName, HttpStatus statusCode, String responseBody, String message) {
        super(message);
        this.clientName = clientName;
        this.statusCode = statusCode;
        this.responseBody = responseBody;
        this.errorCode = null;
    }

    public RestClientException(String clientName, HttpStatus statusCode, String responseBody, String message, Throwable cause) {
        super(message, cause);
        this.clientName = clientName;
        this.statusCode = statusCode;
        this.responseBody = responseBody;
        this.errorCode = null;
    }

    public RestClientException(String message) {
        super(message);
        this.clientName = null;
        this.statusCode = null;
        this.responseBody = null;
        this.errorCode = null;
    }

    public RestClientException(String message, Throwable cause) {
        super(message, cause);
        this.clientName = null;
        this.statusCode = null;
        this.responseBody = null;
        this.errorCode = null;
    }

    public RestClientException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public RestClientException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
} 