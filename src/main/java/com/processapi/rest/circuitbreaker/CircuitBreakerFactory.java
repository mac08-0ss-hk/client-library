package com.processapi.rest.circuitbreaker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

@Slf4j
@Component
public class CircuitBreakerFactory {
    private final Map<String, CircuitBreaker> circuitBreakers = new ConcurrentHashMap<>();

    public CircuitBreaker getCircuitBreaker(String name, CircuitBreakerConfig config) {
        return circuitBreakers.computeIfAbsent(name, k -> {
            log.info("Creating circuit breaker for: {}", name);
            return new CircuitBreaker(
                name,
                config.getFailureThreshold(),
                Duration.ofMillis(config.getResetTimeout()),
                Duration.ofMillis(config.getHalfOpenTimeout()),
                () -> true // Default health check that always returns true
            );
        });
    }

    public CircuitBreaker getCircuitBreaker(String name, Supplier<Boolean> healthCheck) {
        return circuitBreakers.computeIfAbsent(name, k -> {
            log.info("Creating circuit breaker for: {}", name);
            return new CircuitBreaker(
                name,
                5, // Default failure threshold
                Duration.ofMillis(60000), // Default reset timeout
                Duration.ofMillis(30000), // Default half-open timeout
                healthCheck
            );
        });
    }

    public void removeCircuitBreaker(String name) {
        circuitBreakers.remove(name);
    }
} 