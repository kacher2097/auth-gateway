package com.authenhub.config.mail;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.util.Properties;

@Slf4j
@Getter
@Setter
@EnableWebMvc
@Configuration
public class MailConfig {

    @Value("${mail.createSubject}")
    private String createSubject;

    @Value("${mail.createUserHtmlFile}")
    private String createUserHtmlFile;

    @Value("${mail.from}")
    private String from;

    @Value("${mail.host}")
    private String host;

    @Value("${mail.hotline}")
    private String hotline;

    @Value("${mail.htmlFile}")
    private String htmlFile;

    @Value("${mail.logoLocation}")
    private String logoLocation;

    @Value("${mail.mailLink}")
    private String mailLink;

    @Value("${mail.mailLinkSecretKey}")
    private String mailLinkSecretKey;

    @Value("${mail.mailName}")
    private String mailName;

    @Value("${mail.password}")
    private String password;

    @Value("${mail.port}")
    private int port;

    @Value("${mail.protocol}")
    private String protocol;

    @Value("${mail.smtpAuth}")
    private boolean smtpAuth;

    @Value("${mail.sslEnable}")
    private boolean sslEnable;

    @Value("${mail.starttlsEnable}")
    private boolean starttlsEnable;

    @Value("${mail.subject}")
    private String subject;

    @Value("${mail.username}")
    private String username;

    @Value("${mail.thymeleaf.checkTemplate}")
    private Boolean thymeleafCheckTemplate;

    @Value("${mail.thymeleaf.checkTemplateLocation}")
    private Boolean thymeleafCheckTemplateLocation;

    @Value("${mail.thymeleaf.enabled}")
    private Boolean thymeleafEnabled;

    @Value("${mail.thymeleaf.prefix}")
    private String thymeleafPrefix;

    @Value("${mail.thymeleaf.suffix}")
    private String thymeleafSuffix;


    @Bean
    public JavaMailSender javaMailSender() {
        log.info("Begin init JavaMailSender");
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(port);

        mailSender.setUsername(username);
        mailSender.setPassword(password);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", protocol);
        props.put("mail.smtp.auth", smtpAuth);
        props.put("mail.smtp.starttls.enable", starttlsEnable);
        props.put("mail.smtp.ssl.enable", sslEnable);
        props.put("mail.debug", "true");
        log.info("Create JavaMailSender successfully");
        return mailSender;
    }
}
