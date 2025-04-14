package com.authenhub.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private JavaMailSender mailSender;

    @PostConstruct
    public void init() {
        log.info("EmailService initialized with fromEmail: {} and frontendUrl: {}", fromEmail, frontendUrl);
    }

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public void sendPasswordResetEmail(String to, String token) {
        try {
            log.info("Preparing to send password reset email to: {}", to);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject("Password Reset Request");

            String resetUrl = frontendUrl + "/reset-password?token=" + token;

            message.setText("To reset your password, click the link below:\n\n" + resetUrl +
                    "\n\nIf you did not request a password reset, please ignore this email.");

            log.info("Sending email with reset URL: {}", resetUrl);
            mailSender.send(message);
            log.info("Password reset email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send password reset email to: {}", to, e);
        }
    }
}
