package com.processapi.rest.circuitbreaker;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "rest-client.circuit-breaker")
public class CircuitBreakerConfig {
    private int failureThreshold = 5;
    private long resetTimeout = 60000; // 1 minute
    private long halfOpenTimeout = 30000; // 30 seconds
} 