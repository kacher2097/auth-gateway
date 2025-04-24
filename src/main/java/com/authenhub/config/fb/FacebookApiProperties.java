package com.authenhub.config.fb;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "facebook.api")
@Data
public class FacebookApiProperties {
    private String apiBaseUrl = "https://graph.facebook.com/v18.0";
    private int connectTimeout = 5000;
    private int readTimeout = 5000;
    private int maxRetries = 3;
}
