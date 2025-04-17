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
public class PaymentRequest {
    private String orderId;
    private String orderInfo;
    private long amount;
    private String currency;
    private String returnUrl;
    private String cancelUrl;
    private String ipAddress;
    private String userId;
    private Map<String, String> extraData;
}
