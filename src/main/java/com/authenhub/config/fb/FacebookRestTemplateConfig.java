package com.authenhub.config.fb;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
@EnableRetry
public class FacebookRestTemplateConfig {
    
    private final FacebookApiProperties apiProperties;
    
    public FacebookRestTemplateConfig(FacebookApiProperties apiProperties) {
        this.apiProperties = apiProperties;
    }
    
    @Bean(name = "facebookRestTemplate")
    public RestTemplate facebookRestTemplate() {
        return new RestTemplateBuilder()
                .requestFactory(this::clientHttpRequestFactory)
                .setConnectTimeout(Duration.ofMillis(apiProperties.getConnectTimeout()))
                .setReadTimeout(Duration.ofMillis(apiProperties.getReadTimeout()))
                .build();
    }
    
    private ClientHttpRequestFactory clientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(apiProperties.getConnectTimeout());
        factory.setReadTimeout(apiProperties.getReadTimeout());
        return factory;
    }
}
