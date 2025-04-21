package com.authenhub.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "access_logs")
public class AccessLog {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "access_log_seq")
    @SequenceGenerator(name = "access_log_seq", sequenceName = "access_log_sequence", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "username")
    private String username;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent", length = 1000)
    private String userAgent;

    @Column(name = "endpoint", length = 500)
    private String endpoint;

    @Column(name = "method")
    private String method;

    @Column(name = "status_code")
    private int statusCode;

    @Column(name = "timestamp")
    private Timestamp timestamp;

    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "referrer", length = 1000)
    private String referrer;

    @Column(name = "response_time_ms")
    private long responseTimeMs;

    // For analytics
    @Column(name = "country")
    private String country;

    @Column(name = "city")
    private String city;

    @Column(name = "browser")
    private String browser;

    @Column(name = "operating_system")
    private String operatingSystem;

    @Column(name = "device_type")
    private String deviceType; // MOBILE, DESKTOP, TABLET

    /**
     * Convert from MongoDB entity to JPA entity
     */
    public static AccessLog fromMongo(com.authenhub.entity.mongo.AccessLog accessLog) {
        return AccessLog.builder()
                .userId(accessLog.getUserId())
                .username(accessLog.getUsername())
                .ipAddress(accessLog.getIpAddress())
                .userAgent(accessLog.getUserAgent())
                .endpoint(accessLog.getEndpoint())
                .method(accessLog.getMethod())
                .statusCode(accessLog.getStatusCode())
                .timestamp(accessLog.getTimestamp())
                .sessionId(accessLog.getSessionId())
                .referrer(accessLog.getReferrer())
                .responseTimeMs(accessLog.getResponseTimeMs())
                .country(accessLog.getCountry())
                .city(accessLog.getCity())
                .browser(accessLog.getBrowser())
                .operatingSystem(accessLog.getOperatingSystem())
                .deviceType(accessLog.getDeviceType())
                .build();
    }

    /**
     * Convert to MongoDB entity
     */
    public com.authenhub.entity.mongo.AccessLog toMongo() {
        com.authenhub.entity.mongo.AccessLog accessLog = new com.authenhub.entity.mongo.AccessLog();
        accessLog.setId(this.id != null ? this.id.toString() : null);
        accessLog.setUserId(this.userId);
        accessLog.setUsername(this.username);
        accessLog.setIpAddress(this.ipAddress);
        accessLog.setUserAgent(this.userAgent);
        accessLog.setEndpoint(this.endpoint);
        accessLog.setMethod(this.method);
        accessLog.setStatusCode(this.statusCode);
        accessLog.setTimestamp(this.timestamp);
        accessLog.setSessionId(this.sessionId);
        accessLog.setReferrer(this.referrer);
        accessLog.setResponseTimeMs(this.responseTimeMs);
        accessLog.setCountry(this.country);
        accessLog.setCity(this.city);
        accessLog.setBrowser(this.browser);
        accessLog.setOperatingSystem(this.operatingSystem);
        accessLog.setDeviceType(this.deviceType);
        return accessLog;
    }
}
