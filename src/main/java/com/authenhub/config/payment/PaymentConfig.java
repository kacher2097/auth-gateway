package com.authenhub.config.payment;

import com.authenhub.service.payment.provider.MoMoProvider;
import com.authenhub.service.payment.provider.PaymentProvider;
import com.authenhub.service.payment.provider.VNPayProvider;
import com.authenhub.service.payment.provider.ViettelPayProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class PaymentConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public Map<String, PaymentProvider> paymentProviders(
            VNPayProvider vnPayProvider,
            MoMoProvider moMoProvider,
            ViettelPayProvider viettelPayProvider) {
        Map<String, PaymentProvider> providers = new HashMap<>();
        providers.put("VNPAY", vnPayProvider);
        providers.put("MOMO", moMoProvider);
        providers.put("VIETTELPAY", viettelPayProvider);
        return providers;
    }
}
