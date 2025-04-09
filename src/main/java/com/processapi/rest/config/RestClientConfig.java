package com.processapi.rest.config;

import com.processapi.rest.exception.RestClientErrorHandler;
import com.processapi.rest.interceptor.LoggingRequestInterceptor;
import com.processapi.rest.interceptor.LoggingResponseInterceptor;
import com.processapi.rest.interceptor.RequestInterceptor;
import com.processapi.rest.interceptor.ResponseInterceptor;
import com.processapi.rest.interceptor.RetryInterceptor;
import com.processapi.rest.util.CertificateLogger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RestClientConfig {

    private final ApplicationContext applicationContext;
    private final RestClientProperties properties;
    private final CertificateLogger certificateLogger;

    @Bean
    public Map<String, RestClient> restClients() {
        Map<String, RestClient> clients = new HashMap<>();
        
        for (Map.Entry<String, RestClientProperties.ClientConfig> entry : properties.getClients().entrySet()) {
            String clientName = entry.getKey();
            RestClientProperties.ClientConfig config = entry.getValue();
            
            try {
                CloseableHttpClient httpClient = createHttpClient(config);
                ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
                
                RestClient.Builder builder = RestClient.builder()
                    .baseUrl(config.getBaseUrl())
                    .requestFactory(requestFactory);
                
                clients.put(clientName, builder.build());
                log.info("Created REST client for: {}", clientName);
            } catch (Exception e) {
                log.error("Failed to create REST client for: {}", clientName, e);
            }
        }
        
        return clients;
    }

    private CloseableHttpClient createHttpClient(RestClientProperties.ClientConfig config) throws Exception {
        SSLContext sslContext = null;
        if (config.getSsl() != null) {
            sslContext = createSSLContext(config.getSsl());
        }

        PoolingHttpClientConnectionManager connectionManager = createConnectionManager(config.getConnectionPool());
        
        return HttpClients.custom()
            .setConnectionManager(connectionManager)
            .setSSLSocketFactory(sslContext != null ? new SSLConnectionSocketFactory(sslContext) : null)
            .build();
    }

    private PoolingHttpClientConnectionManager createConnectionManager(RestClientProperties.ConnectionPoolConfig config) {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(config.getMaxTotal());
        connectionManager.setDefaultMaxPerRoute(config.getDefaultMaxPerRoute());
        return connectionManager;
    }

    private SSLContext createSSLContext(RestClientProperties.SSLProperties ssl) throws Exception {
        SSLContextBuilder builder = SSLContextBuilder.create();
        
        if (ssl.getTrustStorePath() != null) {
            KeyStore trustStore = KeyStore.getInstance(ssl.getTrustStoreType());
            trustStore.load(new FileInputStream(new File(ssl.getTrustStorePath())), 
                          ssl.getTrustStorePassword().toCharArray());
            builder.loadTrustMaterial(trustStore, null);
        }
        
        if (ssl.getKeyStorePath() != null) {
            KeyStore keyStore = KeyStore.getInstance(ssl.getKeyStoreType());
            keyStore.load(new FileInputStream(new File(ssl.getKeyStorePath())), 
                         ssl.getKeyStorePassword().toCharArray());
            builder.loadKeyMaterial(keyStore, ssl.getKeyPassword().toCharArray());
        }
        
        return builder.build();
    }

    @Bean
    public List<ClientHttpRequestInterceptor> clientHttpRequestInterceptors() {
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        
        // Add default logging interceptors if enabled
        properties.getClients().forEach(client -> {
            if (client.getInterceptors().isEnableLogging()) {
                interceptors.add(new LoggingRequestInterceptor());
                interceptors.add(new LoggingResponseInterceptor());
            }
            
            // Add custom request interceptors
            client.getInterceptors().getRequestInterceptors().forEach(interceptorName -> {
                RequestInterceptor interceptor = applicationContext.getBean(interceptorName, RequestInterceptor.class);
                interceptors.add((request, body, execution) -> interceptor.intercept(request, body, execution));
            });
            
            // Add custom response interceptors
            client.getInterceptors().getResponseInterceptors().forEach(interceptorName -> {
                ResponseInterceptor interceptor = applicationContext.getBean(interceptorName, ResponseInterceptor.class);
                interceptors.add((request, body, execution) -> {
                    ClientHttpResponse response = execution.execute(request, body);
                    return interceptor.intercept(response);
                });
            });
        });
        
        return interceptors;
    }
} 