package com.processapi.rest.ratelimit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Component
public class RateLimiter {
    private final Map<String, TokenBucket> buckets = new ConcurrentHashMap<>();
    private final RateLimiterConfig config;

    public RateLimiter(RateLimiterConfig config) {
        this.config = config;
    }

    public void checkRateLimit(String clientName) {
        if (!config.isEnabled()) {
            return;
        }

        TokenBucket bucket = buckets.computeIfAbsent(clientName, k -> new TokenBucket(
            config.getPermitsPerSecond(),
            config.getMaxBurstSeconds()
        ));

        if (!bucket.tryConsume()) {
            log.warn("Rate limit exceeded for client: {}", clientName);
            throw new RateLimitException(clientName, config.getPermitsPerSecond(), config.getMaxBurstSeconds());
        }
    }

    private static class TokenBucket {
        private final int permitsPerSecond;
        private final int maxBurstSeconds;
        private final AtomicLong tokens;
        private volatile Instant lastRefill;

        TokenBucket(int permitsPerSecond, int maxBurstSeconds) {
            this.permitsPerSecond = permitsPerSecond;
            this.maxBurstSeconds = maxBurstSeconds;
            this.tokens = new AtomicLong(permitsPerSecond * maxBurstSeconds);
            this.lastRefill = Instant.now();
        }

        boolean tryConsume() {
            refill();
            return tokens.updateAndGet(current -> current > 0 ? current - 1 : current) >= 0;
        }

        private void refill() {
            Instant now = Instant.now();
            long elapsedMillis = Duration.between(lastRefill, now).toMillis();
            if (elapsedMillis < 1000) {
                return;
            }

            long newTokens = (elapsedMillis / 1000) * permitsPerSecond;
            long maxTokens = permitsPerSecond * maxBurstSeconds;
            tokens.updateAndGet(current -> Math.min(maxTokens, current + newTokens));
            lastRefill = now;
        }
    }
} 