package com.authenhub.config.rest;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
public class RestTemplateConfig {

    @Value("${rest.connection.timeout}")
    private int connectionTimeout;

    @Value("${rest.socket.timeout}")
    private int socketTimeout;

    @Value("${pool.max}")
    private int poolMaxTotal;
}
