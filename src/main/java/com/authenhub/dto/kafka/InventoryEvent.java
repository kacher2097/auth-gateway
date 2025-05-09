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
public class InventoryEvent {
    
    private String eventId;
    private String eventType; // CREATED, UPDATED, DELETED, QUANTITY_CHANGED
    private String sku;
    private String productName;
    private Integer oldQuantity;
    private Integer newQuantity;
    private Integer quantityChange;
    private String userId;
    private LocalDateTime timestamp;
    private String notes;
}
