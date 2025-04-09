package com.processapi.rest.client;

import com.processapi.rest.config.RestClientConfig;
import com.processapi.rest.config.RestClientProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public final class RestClientFactory {
    private final RestClientProperties properties;

    @Autowired
    public RestClientFactory(RestClientProperties properties) {
        this.properties = properties;
    }

    public RestClientBase createClient(String clientName) {
        RestClientProperties.ClientConfig clientConfig = properties.getClients().get(clientName);
        if (clientConfig == null) {
            throw new IllegalArgumentException("No configuration found for client: " + clientName);
        }
        return new RestClientBase(clientConfig);
    }
} 