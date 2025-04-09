package com.processapi.rest.client;

import org.springframework.web.client.RestClient;

public abstract class RestClientBase {
    protected abstract RestClient getRestClient(String clientName);
} 