package com.processapi.rest.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "rest-client.retry")
public class RetryConfig {
    private int maxAttempts = 3;
    private long initialInterval = 1000;
    private double multiplier = 2.0;
    private long maxInterval = 10000;
} 