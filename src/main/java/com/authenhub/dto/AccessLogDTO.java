package com.authenhub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessLogDTO {
    private String id;
    private String userId;
    private String username;
    private String ipAddress;
    private String userAgent;
    private String endpoint;
    private String method;
    private int statusCode;
    private Timestamp timestamp;
    private String sessionId;
    private String referrer;
    private long responseTimeMs;

    private String country;
    private String city;
    private String browser;
    private String operatingSystem;
    private String deviceType;
}
