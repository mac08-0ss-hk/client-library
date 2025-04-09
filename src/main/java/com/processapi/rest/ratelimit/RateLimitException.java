package com.processapi.rest.ratelimit;

import lombok.Getter;

@Getter
public class RateLimitException extends RuntimeException {
    private final String clientName;
    private final int permitsPerSecond;
    private final int maxBurstSeconds;

    public RateLimitException(String clientName, int permitsPerSecond, int maxBurstSeconds) {
        super(String.format("Rate limit exceeded for client %s. Permits per second: %d, Max burst seconds: %d",
            clientName, permitsPerSecond, maxBurstSeconds));
        this.clientName = clientName;
        this.permitsPerSecond = permitsPerSecond;
        this.maxBurstSeconds = maxBurstSeconds;
    }
} 