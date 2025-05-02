package com.authenhub.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
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
@Table(name = "free_proxies")
public class FreeProxy {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "proxy_seq")
    @SequenceGenerator(name = "proxy_seq", sequenceName = "proxy_sequence", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(name = "ip_address", nullable = false)
    private String ipAddress;

    @Column(name = "port", nullable = false)
    private int port;

    @Column(name = "protocol", nullable = false)
    private String protocol; // HTTP, HTTPS, SOCKS4, SOCKS5

    @Column(name = "country")
    private String country;

    @Column(name = "city")
    private String city;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "response_time_ms")
    private long responseTimeMs;

    @Column(name = "last_checked")
    private Timestamp lastChecked;

    @Column(name = "created_at")
    private Timestamp createdAt;

    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "created_by")
    private String createdBy; // User ID who added this proxy

    @Column(name = "success_count")
    private int successCount;

    @Column(name = "fail_count")
    private int failCount;

    @Column(name = "uptime")
    private double uptime; // Percentage of successful checks

    @Column(name = "notes", length = 1000)
    private String notes;

    /**
     * Convert from MongoDB entity to JPA entity
     */
    public static FreeProxy fromMongo(com.authenhub.entity.mongo.FreeProxy proxy) {
        return FreeProxy.builder()
                .ipAddress(proxy.getIpAddress())
                .port(proxy.getPort())
                .protocol(proxy.getProtocol())
                .country(proxy.getCountry())
                .city(proxy.getCity())
                .isActive(proxy.isActive())
                .responseTimeMs(proxy.getResponseTimeMs())
                .lastChecked(proxy.getLastChecked())
                .createdAt(proxy.getCreatedAt())
                .updatedAt(proxy.getUpdatedAt())
                .createdBy(proxy.getCreatedBy())
                .successCount(proxy.getSuccessCount())
                .failCount(proxy.getFailCount())
                .uptime(proxy.getUptime())
                .notes(proxy.getNotes())
                .build();
    }

    /**
     * Convert to MongoDB entity
     */
    public com.authenhub.entity.mongo.FreeProxy toMongo() {
        com.authenhub.entity.mongo.FreeProxy proxy = new com.authenhub.entity.mongo.FreeProxy();
        proxy.setId(this.id != null ? this.id.toString() : null);
        proxy.setIpAddress(this.ipAddress);
        proxy.setPort(this.port);
        proxy.setProtocol(this.protocol);
        proxy.setCountry(this.country);
        proxy.setCity(this.city);
        proxy.setActive(this.isActive);
        proxy.setResponseTimeMs(this.responseTimeMs);
        proxy.setLastChecked(this.lastChecked);
        proxy.setCreatedAt(this.createdAt);
        proxy.setUpdatedAt(this.updatedAt);
        proxy.setCreatedBy(this.createdBy);
        proxy.setSuccessCount(this.successCount);
        proxy.setFailCount(this.failCount);
        proxy.setUptime(this.uptime);
        proxy.setNotes(this.notes);
        return proxy;
    }
}
