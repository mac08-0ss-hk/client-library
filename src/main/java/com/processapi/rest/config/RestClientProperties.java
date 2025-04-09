package com.processapi.rest.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

@Data
@ConfigurationProperties(prefix = "rest-client")
public class RestClientProperties {
    private Map<String, ClientConfig> clients;

    @Data
    public static class ClientConfig {
        private String baseUrl;
        private SSLProperties ssl;
        private ConnectionPoolConfig connectionPool;
        private InterceptorsConfig interceptors;
    }

    @Data
    public static class SSLProperties {
        private String trustStorePath;
        private String trustStoreType;
        private String trustStorePassword;
        private String keyStorePath;
        private String keyStoreType;
        private String keyStorePassword;
        private String keyPassword;
    }

    @Data
    public static class ConnectionPoolConfig {
        private int maxTotal = 20;
        private int defaultMaxPerRoute = 10;
        private long timeToLive = 60000;
    }

    @Data
    public static class InterceptorsConfig {
        private boolean enableLogging = true;
        private Map<String, String> requestInterceptors;
        private Map<String, String> responseInterceptors;
    }
} 