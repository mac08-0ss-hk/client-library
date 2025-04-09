package com.processapi.rest.client;

import com.processapi.rest.circuitbreaker.CircuitBreaker;
import com.processapi.rest.circuitbreaker.CircuitBreakerFactory;
import com.processapi.rest.config.RestClientConfig;
import com.processapi.rest.exception.RestClientException;
import com.processapi.rest.interceptor.RequestInterceptor;
import com.processapi.rest.interceptor.ResponseInterceptor;
import com.processapi.rest.ratelimit.RateLimiter;
import com.processapi.rest.ratelimit.RateLimiterConfig;
import com.processapi.rest.timeout.TimeoutManager;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public final class RestClientBase {
    private static final Logger logger = LoggerFactory.getLogger(RestClientBase.class);
    private final CloseableHttpClient httpClient;
    private final RestClientConfig config;
    private final CircuitBreaker circuitBreaker;
    private final RateLimiter rateLimiter;
    private final TimeoutManager timeoutManager;
    private final List<RequestInterceptor> requestInterceptors;
    private final List<ResponseInterceptor> responseInterceptors;

    private RestClientBase() {
        throw new UnsupportedOperationException("This class cannot be instantiated directly. Use RestClientFactory instead.");
    }

    protected abstract RestClient getRestClient(String clientName);
} 