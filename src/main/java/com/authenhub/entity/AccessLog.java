package com.authenhub.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "access_logs")
public class AccessLog {
    @Id
    private String id;
    private String userId;
    private String username;
    private String ipAddress;
    private String userAgent;
    private String endpoint;
    private String method;
    private int statusCode;
    private LocalDateTime timestamp;
    private String sessionId;
    private String referrer;
    private long responseTimeMs;
    
    // For analytics
    private String country;
    private String city;
    private String browser;
    private String operatingSystem;
    private String deviceType; // MOBILE, DESKTOP, TABLET
}
