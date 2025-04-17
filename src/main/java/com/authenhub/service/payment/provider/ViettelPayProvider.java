package com.authenhub.service.payment.provider;

import com.authenhub.bean.payment.PaymentRequest;
import com.authenhub.bean.payment.PaymentResponse;
import com.authenhub.config.payment.ViettelPayConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class ViettelPayProvider implements PaymentProvider {

    private final ViettelPayConfig viettelPayConfig;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public PaymentResponse initializePayment(PaymentRequest request) {
        try {
            // Create request to ViettelPay
            String orderId = request.getOrderId();
            if (orderId == null || orderId.isEmpty()) {
                orderId = String.valueOf(System.currentTimeMillis());
            }
            
            // Format date for ViettelPay
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            String requestTime = dateFormat.format(new Date());
            
            // Create request body
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("merchant_code", viettelPayConfig.getMerchantCode());
            requestBody.put("order_id", orderId);
            requestBody.put("amount", request.getAmount());
            requestBody.put("currency", "VND");
            requestBody.put("return_url", request.getReturnUrl());
            requestBody.put("cancel_url", request.getCancelUrl());
            requestBody.put("notify_url", viettelPayConfig.getNotifyUrl());
            requestBody.put("title", request.getOrderInfo());
            requestBody.put("request_time", requestTime);
            
            // Add extra data if available
            if (request.getExtraData() != null && !request.getExtraData().isEmpty()) {
                requestBody.put("extra_data", objectMapper.writeValueAsString(request.getExtraData()));
            }
            
            // Create checksum
            String rawChecksum = viettelPayConfig.getMerchantCode() +
                    orderId +
                    request.getAmount() +
                    "VND" +
                    request.getReturnUrl() +
                    request.getCancelUrl() +
                    viettelPayConfig.getNotifyUrl() +
                    request.getOrderInfo() +
                    requestTime +
                    viettelPayConfig.getSecretKey();
            
            String checksum = hmacSHA256(viettelPayConfig.getSecretKey(), rawChecksum);
            requestBody.put("checksum", checksum);
            
            // Send request to ViettelPay
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    viettelPayConfig.getPaymentUrl(), 
                    entity, 
                    Map.class
            );
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                
                if ("00".equals(responseBody.get("status"))) {
                    return PaymentResponse.builder()
                            .success(true)
                            .message("Payment URL generated successfully")
                            .paymentUrl((String) responseBody.get("payment_url"))
                            .orderId(orderId)
                            .transactionId((String) responseBody.get("transaction_id"))
                            .build();
                } else {
                    return PaymentResponse.builder()
                            .success(false)
                            .message("Failed to initialize payment: " + responseBody.get("message"))
                            .build();
                }
            } else {
                return PaymentResponse.builder()
                        .success(false)
                        .message("Failed to initialize payment: Invalid response from ViettelPay")
                        .build();
            }
            
        } catch (Exception e) {
            log.error("Error initializing ViettelPay payment: {}", e.getMessage(), e);
            return PaymentResponse.builder()
                    .success(false)
                    .message("Failed to initialize payment: " + e.getMessage())
                    .build();
        }
    }

    @Override
    public boolean verifyPayment(String callbackData) {
        try {
            // Parse callback data
            Map<String, Object> fields = objectMapper.readValue(callbackData, Map.class);
            
            // Extract checksum
            String receivedChecksum = (String) fields.get("checksum");
            
            // Build raw checksum
            String rawChecksum = viettelPayConfig.getMerchantCode() +
                    fields.get("order_id") +
                    fields.get("amount") +
                    fields.get("currency") +
                    fields.get("transaction_id") +
                    fields.get("status") +
                    fields.get("message") +
                    viettelPayConfig.getSecretKey();
            
            // Calculate checksum
            String calculatedChecksum = hmacSHA256(viettelPayConfig.getSecretKey(), rawChecksum);
            
            // Verify checksum and status
            return receivedChecksum.equals(calculatedChecksum) && "00".equals(fields.get("status"));
            
        } catch (Exception e) {
            log.error("Error verifying ViettelPay payment: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public String getProviderName() {
        return "VIETTELPAY";
    }

    @Override
    public boolean isAvailable() {
        return viettelPayConfig != null && 
               viettelPayConfig.getMerchantCode() != null && 
               !viettelPayConfig.getMerchantCode().isEmpty() &&
               viettelPayConfig.getSecretKey() != null && 
               !viettelPayConfig.getSecretKey().isEmpty();
    }
    
    private String hmacSHA256(String key, String data) {
        try {
            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256Hmac.init(secretKeySpec);
            byte[] hmacData = sha256Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hmacData);
        } catch (Exception e) {
            log.error("Error generating HMAC-SHA256: {}", e.getMessage(), e);
            return "";
        }
    }
    
    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
