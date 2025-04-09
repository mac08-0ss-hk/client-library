package com.processapi.rest.interceptor;

import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * Interface for intercepting and modifying HTTP responses after they are received.
 * Implement this interface to add custom response modification logic.
 */
@FunctionalInterface
public interface ResponseInterceptor {
    /**
     * Intercept and modify the HTTP response after it is received.
     *
     * @param response the HTTP response to be modified
     * @return the modified HTTP response
     * @throws IOException if an I/O error occurs
     */
    ClientHttpResponse intercept(ClientHttpResponse response) throws IOException;
} 