package com.authenhub.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

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

    @Value("${google.spreadsheetId}")
    private String spreadsheetIdNew;

    @Value("${google.clientId}")
    private String clientId;

    @Value("${google.clientSecret}")
    private String clientSecret;

//    @Value("${google.username}")
//    private String password;
}
