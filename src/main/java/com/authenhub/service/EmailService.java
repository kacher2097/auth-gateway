package com.authenhub.service;

import com.authenhub.bean.mail.MailContent;
import com.authenhub.config.mail.MailConfig;
import com.authenhub.constant.MailConstant;
import com.authenhub.service.interfaces.IEmailService;
import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jboss.logging.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.File;
import java.nio.charset.StandardCharsets;

import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.TOKEN;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService implements IEmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final MailConfig mailConfig;

    @PostConstruct
    public void init() {
        log.info("EmailService initialized with fromEmail: {} and frontendUrl: {}", fromEmail, frontendUrl);
    }

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Override
    public void sendEmail(MailContent mail, String token, int typeMail) throws MessagingException {
        MDC.put(TOKEN, token);
        log.info("Start sendEmail, {}", mail);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(
                message,
                MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name()
        );

        Context context = new Context();
        context.setVariables(mail.getProps());
        templateEngine.getConfiguration();

        String html = switch (typeMail) {
            case MailConstant.RESET_PASSWORD -> {
                log.info("HTML form is reset password request");
                yield templateEngine.process(mailConfig.getHtmlFile(), context);
            }
            case MailConstant.CREATE_NEW_USER -> {
                log.info("HTML form is create new user request");
                yield templateEngine.process(mailConfig.getCreateUserHtmlFile(), context);
            }
            default -> null;
        };

        mimeMessageHelper.setTo(mail.getMailTo());
        mimeMessageHelper.setFrom(mail.getFrom());
        mimeMessageHelper.setText(html, true);
        mimeMessageHelper.setSubject(mail.getSubject());
        mimeMessageHelper.addInline("logo", new File(mailConfig.getLogoLocation()));

        mailSender.send(message);
        log.info("End sendEmail");
    }

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
