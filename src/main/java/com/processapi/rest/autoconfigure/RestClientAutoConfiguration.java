package com.processapi.rest.autoconfigure;

import com.processapi.rest.config.RestClientConfig;
import com.processapi.rest.config.RestClientProperties;
import com.processapi.rest.util.CertificateLogger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConditionalOnClass(RestClientConfig.class)
@EnableConfigurationProperties(RestClientProperties.class)
@Import(RestClientConfig.class)
public class RestClientAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public CertificateLogger certificateLogger() {
        return new CertificateLogger();
    }
} 