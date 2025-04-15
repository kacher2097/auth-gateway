package com.authenhub.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
public class WpSite {

    @Value("${wordpress.url}")
    private String wordpressUrl;

    @Value("${wordpress.username}")
    private String username;

    @Value("${wordpress.password}")
    private String password;
}
