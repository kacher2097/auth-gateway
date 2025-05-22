package com.authenhub.config.gg;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@Configuration
public class GoogleConfig {

    @Value("${google.credentials}")
    private String credential;

    @Value("${google.token}")
    private String token;

    @Value("${google.spreadsheet.id}")
    private String spreadsheetId;

    @Value("${google.clientId}")
    private String clientId;

    @Value("${google.clientSecret}")
    private String clientSecret;

    @Value("${google.user-info-uri:https://www.googleapis.com/oauth2/v3/userinfo}")
    private String googleUserInfoUrl;

    @Value("${google.redirect-uri:'{baseUrl}/login/oauth2/code/{registrationId}'}")
    private String redirectUri;

    @Value("${google.service-account.key-file:${workdir:.}/config/google-service-account.json}")
    private String serviceAccountKeyFile;

    @Value("${google.service-account.user-email:thanhtuan6897@gmail.com}")
    private String serviceAccountUserEmail;

    @Value("${google.scope}")
    public void setScope(String scopeString) {
        this.scopes = Arrays.asList(scopeString.split(","));
    }

    private List<String> scopes;
}
