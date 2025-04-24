package com.authenhub.bean.proxy;

import com.authenhub.entity.mongo.FreeProxy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProxyResponse {
    private String id;
    private String ipAddress;
    private int port;
    private String protocol;
    private String country;
    private String city;
    private boolean isActive;
    private long responseTimeMs;
    private Timestamp lastChecked;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String createdBy;
    private int successCount;
    private int failCount;
    private double uptime;
    private String notes;

    public static ProxyResponse fromEntity(FreeProxy proxy) {
        return ProxyResponse.builder()
                .id(proxy.getId())
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
}
