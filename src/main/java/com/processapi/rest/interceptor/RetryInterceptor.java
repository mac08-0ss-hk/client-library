package com.processapi.rest.interceptor;

import com.processapi.rest.config.RetryConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RetryInterceptor implements ClientHttpRequestInterceptor {
    private final RetryConfig retryConfig;

    public RetryInterceptor(RetryConfig retryConfig) {
        this.retryConfig = retryConfig;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        int attempts = 0;
        IOException lastException = null;

        while (attempts < retryConfig.getMaxAttempts()) {
            try {
                return execution.execute(request, body);
            } catch (IOException e) {
                lastException = e;
                attempts++;
                log.warn("Request failed (attempt {}/{}): {}", attempts, retryConfig.getMaxAttempts(), e.getMessage());

                if (attempts < retryConfig.getMaxAttempts()) {
                    try {
                        long delay = calculateDelay(attempts);
                        log.info("Waiting {} ms before retry", delay);
                        TimeUnit.MILLISECONDS.sleep(delay);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new IOException("Retry interrupted", ie);
                    }
                }
            }
        }

        throw lastException;
    }

    private long calculateDelay(int attempt) {
        long delay = (long) (retryConfig.getInitialInterval() * Math.pow(retryConfig.getMultiplier(), attempt - 1));
        return Math.min(delay, retryConfig.getMaxInterval());
    }
} 