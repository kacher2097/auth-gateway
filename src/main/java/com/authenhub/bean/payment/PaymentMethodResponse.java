package com.authenhub.bean.payment;

import com.authenhub.entity.mongo.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethodResponse {
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

    public static PaymentMethodResponse fromEntity(PaymentMethod entity) {
        return PaymentMethodResponse.builder()
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
