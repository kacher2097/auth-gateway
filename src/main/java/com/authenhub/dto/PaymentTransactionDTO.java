package com.authenhub.dto;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Timestamp;

@Data
@Document(collection = "payment_transactions")
public class PaymentTransactionDTO {

    @Id
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
