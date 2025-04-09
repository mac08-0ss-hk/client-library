package com.processapi.rest.autoconfigure;

import com.processapi.rest.client.RestClientFactory;
import com.processapi.rest.config.RestClientProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(RestClientProperties.class)
public class RestClientAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RestClientFactory restClientFactory(RestClientProperties properties) {
        return new RestClientFactory(properties);
    }
} 