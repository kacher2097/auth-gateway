package com.authenhub.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesEvent {
    
    private String eventId;
    private String sku;
    private String productName;
    private Integer quantity;
    private Double salePrice;
    private String channel;
    private String orderId;
    private String customerId;
    private String userId;
    private LocalDateTime saleDate;
    private LocalDateTime timestamp;
}
