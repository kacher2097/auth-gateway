package com.authenhub.dto;

import com.authenhub.entity.FreeProxy;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.sql.Timestamp;
import java.util.List;

public class FreeProxyDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        @NotBlank(message = "IP address is required")
        private String ipAddress;

        @NotNull(message = "Port is required")
        @Min(value = 1, message = "Port must be greater than 0")
        @Max(value = 65535, message = "Port must be less than 65536")
        private Integer port;

        @NotBlank(message = "Protocol is required")
        private String protocol;

        private String country;
        private String city;
        private String notes;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private String id;
        private String ipAddress;
        private int port;
        private String protocol;
        private String country;
        private String city;
        private boolean isActive;
        private int responseTimeMs;
        private Timestamp lastChecked;
        private Timestamp createdAt;
        private Timestamp updatedAt;
        private String createdBy;
        private int successCount;
        private int failCount;
        private double uptime;
        private String notes;

        public static Response fromEntity(FreeProxy proxy) {
            return Response.builder()
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

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CheckResult {
        private String id;
        private boolean isWorking;
        private int responseTimeMs;
        private Timestamp checkedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImportRequest {
        private String fileType; // CSV or EXCEL
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImportResult {
        private int totalProcessed;
        private int successCount;
        private int failCount;
        private List<ImportError> errors;
        private List<Response> importedProxies;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImportError {
        private int rowNumber;
        private String errorMessage;
        private String rawData;
    }
}
