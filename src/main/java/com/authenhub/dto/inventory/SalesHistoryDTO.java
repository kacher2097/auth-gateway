package com.authenhub.dto.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesHistoryDTO {
    private Long id;
    private String sku;
    private Integer quantity;
    private LocalDateTime saleDate;
    private Double salePrice;
    private String channel;
    private String orderId;
    private String customerId;
    private LocalDateTime createdAt;
    private String createdBy;
    private String productName; // Added for convenience when displaying sales history
}
