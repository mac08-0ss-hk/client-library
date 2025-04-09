package com.processapi.rest.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * Default implementation of ResponseInterceptor that logs response details.
 */
@Slf4j
public class LoggingResponseInterceptor implements ClientHttpRequestInterceptor {
    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        ClientHttpResponse response = execution.execute(request, body);
        log.info("Response: {} {}", response.getStatusCode(), response.getStatusText());
        log.info("Response Headers: {}", response.getHeaders());
        log.info("Response Body: {}", new String(response.getBody().readAllBytes()));
        return response;
    }
} 