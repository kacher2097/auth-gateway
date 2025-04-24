package com.authenhub.bean.payment;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class PaymentTransactionDTO {
    private String id;
    private String userId;
    private Long amount;
    private String status;
    private String currency;
    private String paymentMethod;
    private String description;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Fields for aggregation results
    private Long totalAmount;
    private Long count;
    private Double averageAmount;
    private Long minAmount;
    private Long maxAmount;
}
