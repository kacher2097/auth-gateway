package com.authenhub.config.payment;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
public class MoMoConfig {

    @Value("${payment.momo.payment-url:https://test-payment.momo.vn/v2/gateway/api/create}")
    private String paymentUrl;

    @Value("${payment.momo.partner-code:}")
    private String partnerCode;

    @Value("${payment.momo.access-key:}")
    private String accessKey;

    @Value("${payment.momo.secret-key:}")
    private String secretKey;

    @Value("${payment.momo.notify-url:}")
    private String notifyUrl;
}
