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
