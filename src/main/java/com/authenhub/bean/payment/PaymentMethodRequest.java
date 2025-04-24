package com.authenhub.bean.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethodRequest {
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
