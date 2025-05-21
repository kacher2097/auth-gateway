package com.authenhub.config.fb;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev")
@Slf4j
public class SSLInitializer {

    @PostConstruct
    public void init() {
        try {
            log.info("Initializing SSL configuration for development environment");
            SSLUtil.turnOffSslChecking();
        } catch (Exception e) {
            log.error("Failed to initialize SSL configuration", e);
        }
    }
}
