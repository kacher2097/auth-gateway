package com.authenhub.service.payment.provider;

import com.authenhub.bean.payment.PaymentRequest;
import com.authenhub.bean.payment.PaymentResponse;
import com.authenhub.config.payment.VNPayConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class VNPayProvider implements PaymentProvider {

    private final VNPayConfig vnPayConfig;

    @Override
    public PaymentResponse initializePayment(PaymentRequest request) {
        try {
            String vnpVersion = "2.1.0";
            String vnpCommand = "pay";
            String vnpTmnCode = vnPayConfig.getTmnCode();
            String vnpCurrCode = "VND";
            String vnpIpAddr = request.getIpAddress();
            String vnpReturnUrl = request.getReturnUrl();
            
            // Create date format for VNPay
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String vnpCreateDate = formatter.format(calendar.getTime());
            
            // Create order ID
            String orderId = request.getOrderId();
            if (orderId == null || orderId.isEmpty()) {
                orderId = String.valueOf(System.currentTimeMillis());
            }
            
            // Build VNPay parameters
            Map<String, String> vnpParams = new HashMap<>();
            vnpParams.put("vnp_Version", vnpVersion);
            vnpParams.put("vnp_Command", vnpCommand);
            vnpParams.put("vnp_TmnCode", vnpTmnCode);
            vnpParams.put("vnp_Amount", String.valueOf(request.getAmount() * 100)); // Convert to smallest currency unit
            vnpParams.put("vnp_CurrCode", vnpCurrCode);
            vnpParams.put("vnp_TxnRef", orderId);
            vnpParams.put("vnp_OrderInfo", request.getOrderInfo());
            vnpParams.put("vnp_OrderType", "other");
            vnpParams.put("vnp_Locale", "vn");
            vnpParams.put("vnp_ReturnUrl", vnpReturnUrl);
            vnpParams.put("vnp_IpAddr", vnpIpAddr);
            vnpParams.put("vnp_CreateDate", vnpCreateDate);
            
            // Add extra data if available
            if (request.getExtraData() != null && !request.getExtraData().isEmpty()) {
                String vnpExtraData = URLEncoder.encode(
                        new String(Base64.getEncoder().encode(
                                request.getExtraData().toString().getBytes(StandardCharsets.UTF_8)
                        ), StandardCharsets.UTF_8), 
                        StandardCharsets.UTF_8.toString()
                );
                vnpParams.put("vnp_OrderType", vnpExtraData);
            }
            
            // Build payment URL
            List<String> fieldNames = new ArrayList<>(vnpParams.keySet());
            Collections.sort(fieldNames);
            
            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();
            
            for (String fieldName : fieldNames) {
                String fieldValue = vnpParams.get(fieldName);
                if (fieldValue != null && !fieldValue.isEmpty()) {
                    // Build hash data
                    hashData.append(fieldName).append('=').append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString())).append('&');
                    
                    // Build query
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.UTF_8.toString()))
                            .append('=')
                            .append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.toString()))
                            .append('&');
                }
            }
            
            // Remove last '&'
            String queryUrl = query.substring(0, query.length() - 1);
            String vnpSecureHash = hmacSHA512(vnPayConfig.getSecretKey(), hashData.substring(0, hashData.length() - 1));
            queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
            
            String paymentUrl = vnPayConfig.getPaymentUrl() + "?" + queryUrl;
            
            return PaymentResponse.builder()
                    .success(true)
                    .message("Payment URL generated successfully")
                    .paymentUrl(paymentUrl)
                    .orderId(orderId)
                    .build();
            
        } catch (Exception e) {
            log.error("Error initializing VNPay payment: {}", e.getMessage(), e);
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
            Map<String, String> fields = new HashMap<>();
            String[] params = callbackData.split("&");
            
            for (String param : params) {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2) {
                    fields.put(keyValue[0], URLEncoder.encode(keyValue[1], StandardCharsets.UTF_8.toString()));
                }
            }
            
            // Remove vnp_SecureHash and vnp_SecureHashType
            String secureHash = fields.get("vnp_SecureHash");
            fields.remove("vnp_SecureHash");
            fields.remove("vnp_SecureHashType");
            
            // Sort fields
            List<String> fieldNames = new ArrayList<>(fields.keySet());
            Collections.sort(fieldNames);
            
            // Build hash data
            StringBuilder hashData = new StringBuilder();
            for (String fieldName : fieldNames) {
                String fieldValue = fields.get(fieldName);
                if (fieldValue != null && !fieldValue.isEmpty()) {
                    hashData.append(fieldName).append('=').append(fieldValue).append('&');
                }
            }
            
            // Remove last '&'
            String hash = hashData.substring(0, hashData.length() - 1);
            
            // Calculate secure hash
            String calculatedHash = hmacSHA512(vnPayConfig.getSecretKey(), hash);
            
            // Verify hash
            return secureHash.equals(calculatedHash);
            
        } catch (Exception e) {
            log.error("Error verifying VNPay payment: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public String getProviderName() {
        return "VNPAY";
    }

    @Override
    public boolean isAvailable() {
        return vnPayConfig != null && 
               vnPayConfig.getTmnCode() != null && 
               !vnPayConfig.getTmnCode().isEmpty() &&
               vnPayConfig.getSecretKey() != null && 
               !vnPayConfig.getSecretKey().isEmpty();
    }
    
    private String hmacSHA512(String key, String data) {
        try {
            Mac sha512Hmac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "HmacSHA512");
            sha512Hmac.init(secretKeySpec);
            byte[] hmacData = sha512Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hmacData);
        } catch (Exception e) {
            log.error("Error generating HMAC-SHA512: {}", e.getMessage(), e);
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
