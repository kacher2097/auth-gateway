package com.authenhub.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "free_proxies")
public class FreeProxy {
    @Id
    private String id;
    private String ipAddress;
    private int port;
    private String protocol; // HTTP, HTTPS, SOCKS4, SOCKS5
    private String country;
    private String city;
    private boolean isActive;
    private int responseTimeMs;
    private LocalDateTime lastChecked;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy; // User ID who added this proxy
    private int successCount;
    private int failCount;
    private double uptime; // Percentage of successful checks
    private String notes;
}
