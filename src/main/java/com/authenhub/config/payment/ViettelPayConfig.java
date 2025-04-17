package com.authenhub.config.payment;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
public class ViettelPayConfig {

    @Value("${payment.viettelpay.payment-url:https://sandbox.viettel.vn/PaymentGateway/payment}")
    private String paymentUrl;

    @Value("${payment.viettelpay.merchant-code:}")
    private String merchantCode;

    @Value("${payment.viettelpay.secret-key:}")
    private String secretKey;

    @Value("${payment.viettelpay.notify-url:}")
    private String notifyUrl;
}
