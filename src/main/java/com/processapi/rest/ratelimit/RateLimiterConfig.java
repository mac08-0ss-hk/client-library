package com.processapi.rest.ratelimit;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "rest-client.rate-limit")
public class RateLimiterConfig {
    private int permitsPerSecond = 10;
    private int maxBurstSeconds = 1;
    private boolean enabled = true;
} 