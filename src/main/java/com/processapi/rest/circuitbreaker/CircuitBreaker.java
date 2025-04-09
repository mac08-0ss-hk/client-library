package com.processapi.rest.circuitbreaker;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

@Slf4j
public class CircuitBreaker {
    private final String name;
    private final int failureThreshold;
    private final Duration resetTimeout;
    private final Duration halfOpenTimeout;
    private final Supplier<Boolean> healthCheck;
    
    private final AtomicInteger failureCount = new AtomicInteger(0);
    private final AtomicReference<State> state = new AtomicReference<>(State.CLOSED);
    private final AtomicReference<Instant> lastFailureTime = new AtomicReference<>();
    private final AtomicReference<Instant> halfOpenTime = new AtomicReference<>();

    public enum State {
        CLOSED, OPEN, HALF_OPEN
    }

    public CircuitBreaker(String name, int failureThreshold, Duration resetTimeout, 
                         Duration halfOpenTimeout, Supplier<Boolean> healthCheck) {
        this.name = name;
        this.failureThreshold = failureThreshold;
        this.resetTimeout = resetTimeout;
        this.halfOpenTimeout = halfOpenTimeout;
        this.healthCheck = healthCheck;
    }

    public <T> T execute(Supplier<T> operation) {
        if (isOpen()) {
            if (shouldAttemptReset()) {
                return attemptReset(operation);
            }
            throw new CircuitBreakerOpenException("Circuit breaker is open for " + name);
        }

        try {
            T result = operation.get();
            onSuccess();
            return result;
        } catch (Exception e) {
            onFailure();
            throw e;
        }
    }

    private boolean isOpen() {
        return state.get() == State.OPEN;
    }

    private boolean shouldAttemptReset() {
        if (state.get() == State.OPEN) {
            Instant now = Instant.now();
            Instant lastFailure = lastFailureTime.get();
            return lastFailure != null && 
                   Duration.between(lastFailure, now).compareTo(resetTimeout) > 0;
        }
        return false;
    }

    private <T> T attemptReset(Supplier<T> operation) {
        if (state.compareAndSet(State.OPEN, State.HALF_OPEN)) {
            halfOpenTime.set(Instant.now());
            try {
                T result = operation.get();
                onSuccess();
                return result;
            } catch (Exception e) {
                onFailure();
                throw e;
            }
        }
        throw new CircuitBreakerOpenException("Circuit breaker is open for " + name);
    }

    private void onSuccess() {
        failureCount.set(0);
        state.set(State.CLOSED);
        lastFailureTime.set(null);
        halfOpenTime.set(null);
    }

    private void onFailure() {
        lastFailureTime.set(Instant.now());
        if (state.get() == State.HALF_OPEN) {
            state.set(State.OPEN);
            return;
        }

        int failures = failureCount.incrementAndGet();
        if (failures >= failureThreshold) {
            state.set(State.OPEN);
            log.warn("Circuit breaker opened for {} after {} failures", name, failures);
        }
    }

    public State getState() {
        return state.get();
    }

    public int getFailureCount() {
        return failureCount.get();
    }

    public boolean isHealthy() {
        return healthCheck.get();
    }
} 