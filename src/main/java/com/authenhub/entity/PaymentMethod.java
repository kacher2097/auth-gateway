package com.authenhub.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Timestamp;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "payment_methods")
public class PaymentMethod {
    @Id
    private String id;
    
    private String name;            // Name of the payment method (e.g., "VNPay", "MoMo", "ViettelPay")
    private String displayName;     // Display name for the payment method
    private String description;     // Description of the payment method
    private String providerType;    // Type of provider (VNPAY, MOMO, VIETTELPAY)
    private boolean isActive;       // Whether the payment method is active
    private Map<String, String> config; // Configuration parameters for the payment method
    private String iconUrl;         // URL to the payment method icon
    private double feePercentage;   // Fee percentage for the payment method
    private long fixedFee;          // Fixed fee amount in smallest currency unit (e.g., VND)
    private String currency;        // Currency code (e.g., VND)
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
