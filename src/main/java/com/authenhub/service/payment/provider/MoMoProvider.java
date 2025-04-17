package com.authenhub.service.payment.provider;

import com.authenhub.bean.payment.PaymentRequest;
import com.authenhub.bean.payment.PaymentResponse;
import com.authenhub.config.payment.MoMoConfig;
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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class MoMoProvider implements PaymentProvider {

    private final MoMoConfig moMoConfig;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public PaymentResponse initializePayment(PaymentRequest request) {
        try {
            // Create request to MoMo
            String requestId = UUID.randomUUID().toString();
            String orderId = request.getOrderId();
            if (orderId == null || orderId.isEmpty()) {
                orderId = String.valueOf(System.currentTimeMillis());
            }
            
            // Create request body
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("partnerCode", moMoConfig.getPartnerCode());
            requestBody.put("accessKey", moMoConfig.getAccessKey());
            requestBody.put("requestId", requestId);
            requestBody.put("amount", request.getAmount());
            requestBody.put("orderId", orderId);
            requestBody.put("orderInfo", request.getOrderInfo());
            requestBody.put("returnUrl", request.getReturnUrl());
            requestBody.put("notifyUrl", moMoConfig.getNotifyUrl());
            requestBody.put("requestType", "captureMoMoWallet");
            requestBody.put("extraData", request.getExtraData() != null ? 
                    objectMapper.writeValueAsString(request.getExtraData()) : "");
            
            // Create signature
            String rawSignature = "accessKey=" + moMoConfig.getAccessKey() +
                    "&amount=" + request.getAmount() +
                    "&extraData=" + requestBody.get("extraData") +
                    "&ipnUrl=" + moMoConfig.getNotifyUrl() +
                    "&orderId=" + orderId +
                    "&orderInfo=" + request.getOrderInfo() +
                    "&partnerCode=" + moMoConfig.getPartnerCode() +
                    "&redirectUrl=" + request.getReturnUrl() +
                    "&requestId=" + requestId +
                    "&requestType=captureMoMoWallet";
            
            String signature = hmacSHA256(moMoConfig.getSecretKey(), rawSignature);
            requestBody.put("signature", signature);
            
            // Send request to MoMo
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(
                    moMoConfig.getPaymentUrl(), 
                    entity, 
                    Map.class
            );
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                
                if ("0".equals(responseBody.get("errorCode"))) {
                    return PaymentResponse.builder()
                            .success(true)
                            .message("Payment URL generated successfully")
                            .paymentUrl((String) responseBody.get("payUrl"))
                            .orderId(orderId)
                            .transactionId((String) responseBody.get("transId"))
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
                        .message("Failed to initialize payment: Invalid response from MoMo")
                        .build();
            }
            
        } catch (Exception e) {
            log.error("Error initializing MoMo payment: {}", e.getMessage(), e);
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
            
            // Extract signature
            String receivedSignature = (String) fields.get("signature");
            
            // Build raw signature
            String rawSignature = "accessKey=" + moMoConfig.getAccessKey() +
                    "&amount=" + fields.get("amount") +
                    "&extraData=" + fields.get("extraData") +
                    "&message=" + fields.get("message") +
                    "&orderId=" + fields.get("orderId") +
                    "&orderInfo=" + fields.get("orderInfo") +
                    "&orderType=" + fields.get("orderType") +
                    "&partnerCode=" + fields.get("partnerCode") +
                    "&payType=" + fields.get("payType") +
                    "&requestId=" + fields.get("requestId") +
                    "&responseTime=" + fields.get("responseTime") +
                    "&resultCode=" + fields.get("resultCode") +
                    "&transId=" + fields.get("transId");
            
            // Calculate signature
            String calculatedSignature = hmacSHA256(moMoConfig.getSecretKey(), rawSignature);
            
            // Verify signature
            return receivedSignature.equals(calculatedSignature) && "0".equals(fields.get("resultCode"));
            
        } catch (Exception e) {
            log.error("Error verifying MoMo payment: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public String getProviderName() {
        return "MOMO";
    }

    @Override
    public boolean isAvailable() {
        return moMoConfig != null && 
               moMoConfig.getPartnerCode() != null && 
               !moMoConfig.getPartnerCode().isEmpty() &&
               moMoConfig.getAccessKey() != null && 
               !moMoConfig.getAccessKey().isEmpty() &&
               moMoConfig.getSecretKey() != null && 
               !moMoConfig.getSecretKey().isEmpty();
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
