package com.authenhub.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * A mock implementation of email service that just logs emails instead of sending them.
 * This is useful for development and testing environments.
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "app.use-mock-email", havingValue = "true")
public class MockEmailService {

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public void sendPasswordResetEmail(String to, String token) {
        String resetUrl = frontendUrl + "/reset-password?token=" + token;

        log.info("==================== MOCK EMAIL ====================");
        log.info("To: {}", to);
        log.info("Subject: Password Reset Request");
        log.info("Body:");
        log.info("To reset your password, click the link below:");
        log.info("{}", resetUrl);
        log.info("If you did not request a password reset, please ignore this email.");
        log.info("====================================================");
    }
}
