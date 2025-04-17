package com.authenhub.config.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RestTemplateManager {

    private final RestTemplateConfig restTemplateConfig;

    @Bean
    public PoolingHttpClientConnectionManager poolingHttpClientConnectionManager() {
        PoolingHttpClientConnectionManager poolingConnectionManager = new PoolingHttpClientConnectionManager();
        poolingConnectionManager.setMaxTotal(restTemplateConfig.getPoolMaxTotal());
        return poolingConnectionManager;
    }

    @Bean
    public RequestConfig requestConfig() {
        return RequestConfig.custom()
            .setConnectTimeout(restTemplateConfig.getConnectionTimeout())
            .setSocketTimeout(restTemplateConfig.getSocketTimeout())
            .build();
    }

    @Bean
    public CloseableHttpClient httpClient(
        PoolingHttpClientConnectionManager poolingHttpClientConnectionManager,
        RequestConfig requestConfig
    ) {
        return HttpClientBuilder
            .create()
            .setConnectionManager(poolingHttpClientConnectionManager)
            .setDefaultRequestConfig(requestConfig)
            .build();
    }

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory httpClient = new SimpleClientHttpRequestFactory();
        httpClient.setConnectTimeout(restTemplateConfig.getConnectionTimeout());
        httpClient.setReadTimeout(restTemplateConfig.getSocketTimeout());
        return new RestTemplate(httpClient);
    }

}
