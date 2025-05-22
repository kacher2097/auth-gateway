package com.authenhub.config.fb;

import java.util.Arrays;
import java.util.List;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@ConfigurationProperties(prefix = "facebook")
public class FacebookConfig {
    @Value("${facebook.client-id}")
    private String clientId;

    @Value("${facebook.client-secret}")
    private String clientSecret;

    @Value("${facebook.redirect-uri}")
    private String redirectUri;

    private List<String> scope;

    @Value("${facebook.scope}")
    public void setScope(String scopeString) {
        this.scope = Arrays.asList(scopeString.split(","));
    }

    @Value("${facebook.user-info-uri}")
    private String userInfoUri;

    @Value("${facebook.authorization-base-url}")
    private String authorizationBaseUrl;

    @Value("${facebook.token-base-url}")
    private String tokenBaseUrl;

    @Value("${facebook.api-base-url}")
    private String apiBaseUrl;

    @Value("${facebook.max-retries}")
    private Integer maxRetries;

    @Value("${facebook.connect-timeout}")
    private Integer connectTimeout;

    @Value("${facebook.read-timeout}")
    private Integer readTimeout;

}
