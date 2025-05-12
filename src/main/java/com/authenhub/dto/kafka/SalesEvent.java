package com.authenhub.dto.kafka;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
