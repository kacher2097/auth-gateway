package com.authenhub.dto;

import com.authenhub.entity.mongo.PaymentMethod;
import com.authenhub.utils.TimestampUtils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Map;

public class PaymentMethodDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        private String name;
        private String displayName;
        private String description;
        private String providerType;
        private boolean isActive;
        private Map<String, String> config;
        private String iconUrl;
        private double feePercentage;
        private long fixedFee;
        private String currency;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private String id;
        private String name;
        private String displayName;
        private String description;
        private String providerType;
        private boolean isActive;
        private Map<String, String> config;
        private String iconUrl;
        private double feePercentage;
        private long fixedFee;
        private String currency;
        private Timestamp createdAt;
        private Timestamp updatedAt;

        public static Response fromEntity(PaymentMethod entity) {
            return Response.builder()
                    .id(entity.getId())
                    .name(entity.getName())
                    .displayName(entity.getDisplayName())
                    .description(entity.getDescription())
                    .providerType(entity.getProviderType())
                    .isActive(entity.isActive())
                    .config(entity.getConfig())
                    .iconUrl(entity.getIconUrl())
                    .feePercentage(entity.getFeePercentage())
                    .fixedFee(entity.getFixedFee())
                    .currency(entity.getCurrency())
                    .createdAt(entity.getCreatedAt())
                    .updatedAt(entity.getUpdatedAt())
                    .build();
        }
    }

    public static PaymentMethod toEntity(Request request) {
        return PaymentMethod.builder()
                .name(request.getName())
                .displayName(request.getDisplayName())
                .description(request.getDescription())
                .providerType(request.getProviderType())
                .isActive(request.isActive())
                .config(request.getConfig())
                .iconUrl(request.getIconUrl())
                .feePercentage(request.getFeePercentage())
                .fixedFee(request.getFixedFee())
                .currency(request.getCurrency())
                .createdAt(TimestampUtils.now())
                .updatedAt(TimestampUtils.now())
                .build();
    }
}
