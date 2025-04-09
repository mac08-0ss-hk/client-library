package com.processapi.rest.timeout;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.client.ClientHttpRequestFactory;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class TimeoutManager {
    private final Map<String, OperationTimeout> operationTimeouts = new ConcurrentHashMap<>();
    private final AtomicLong totalRequests = new AtomicLong(0);
    private final AtomicLong timeoutCount = new AtomicLong(0);
    private final Duration defaultTimeout;
    private final double timeoutAdjustmentFactor;
    private final Duration minTimeout;
    private final Duration maxTimeout;

    public TimeoutManager(Duration defaultTimeout, double timeoutAdjustmentFactor, 
                         Duration minTimeout, Duration maxTimeout) {
        this.defaultTimeout = defaultTimeout;
        this.timeoutAdjustmentFactor = timeoutAdjustmentFactor;
        this.minTimeout = minTimeout;
        this.maxTimeout = maxTimeout;
    }

    public Duration getTimeout(String operation) {
        OperationTimeout timeout = operationTimeouts.computeIfAbsent(operation, 
            k -> new OperationTimeout(defaultTimeout));
        return timeout.getCurrentTimeout();
    }

    public void recordOperation(String operation, Duration executionTime, boolean timedOut) {
        totalRequests.incrementAndGet();
        if (timedOut) {
            timeoutCount.incrementAndGet();
        }

        OperationTimeout timeout = operationTimeouts.get(operation);
        if (timeout != null) {
            timeout.adjustTimeout(executionTime, timedOut);
        }
    }

    public double getTimeoutRate() {
        long total = totalRequests.get();
        return total > 0 ? (double) timeoutCount.get() / total : 0.0;
    }

    private class OperationTimeout {
        private Duration currentTimeout;
        private final AtomicLong successCount = new AtomicLong(0);
        private final AtomicLong failureCount = new AtomicLong(0);

        public OperationTimeout(Duration initialTimeout) {
            this.currentTimeout = initialTimeout;
        }

        public Duration getCurrentTimeout() {
            return currentTimeout;
        }

        public void adjustTimeout(Duration executionTime, boolean timedOut) {
            if (timedOut) {
                failureCount.incrementAndGet();
                // Increase timeout
                currentTimeout = Duration.ofMillis(
                    Math.min(maxTimeout.toMillis(), 
                    (long) (currentTimeout.toMillis() * (1 + timeoutAdjustmentFactor))));
            } else {
                successCount.incrementAndGet();
                // Decrease timeout if we're consistently successful
                if (successCount.get() % 10 == 0) { // Adjust every 10 successful requests
                    currentTimeout = Duration.ofMillis(
                        Math.max(minTimeout.toMillis(), 
                        (long) (currentTimeout.toMillis() * (1 - timeoutAdjustmentFactor))));
                }
            }
        }
    }
} 