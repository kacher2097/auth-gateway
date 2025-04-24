package com.authenhub.config.fb;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "facebook.oauth")
@Data
public class FacebookOAuthConfig {
    private String clientId;
    private String clientSecret;
    private String authorizationBaseUrl = "https://www.facebook.com/v18.0/dialog/oauth";
    private String tokenBaseUrl = "https://graph.facebook.com/v18.0/oauth/access_token";
    private String defaultScope = "email,public_profile,pages_show_list,pages_read_engagement,pages_manage_posts,pages_manage_engagement";
}
