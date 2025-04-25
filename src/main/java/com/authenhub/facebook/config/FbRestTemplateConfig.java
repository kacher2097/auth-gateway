package com.authenhub.facebook.config;

import com.authenhub.config.fb.FacebookApiProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.web.client.RestTemplate;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;

@Configuration
@EnableRetry
public class FbRestTemplateConfig {

    private final FacebookApiProperties apiProperties;
    private final LoggingRequestInterceptor loggingRequestInterceptor;

    public FbRestTemplateConfig(FacebookApiProperties apiProperties, LoggingRequestInterceptor loggingRequestInterceptor) {
        this.apiProperties = apiProperties;
        this.loggingRequestInterceptor = loggingRequestInterceptor;
    }

    @Bean(name = "facebookRestTemplate")
    public RestTemplate facebookRestTemplate() {
        // Sử dụng SimpleClientHttpRequestFactory thay vì HttpComponentsClientHttpRequestFactory
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(5000);

        // Tạo RestTemplate với SimpleClientHttpRequestFactory
        BufferingClientHttpRequestFactory bufferingFactory = new BufferingClientHttpRequestFactory(factory);
        RestTemplate restTemplate = new RestTemplate(bufferingFactory);

        // Thêm interceptor để ghi log request và response
        restTemplate.setInterceptors(Collections.singletonList(loggingRequestInterceptor));

        return restTemplate;
    }
}
