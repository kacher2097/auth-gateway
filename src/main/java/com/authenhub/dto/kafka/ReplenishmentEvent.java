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
public class ReplenishmentEvent {
    
    private String eventId;
    private String eventType; // SUGGESTED, APPROVED, REJECTED
    private Long suggestionId;
    private String sku;
    private String productName;
    private Integer suggestedQuantity;
    private Integer approvedQuantity;
    private String reason;
    private String status;
    private String userId;
    private LocalDateTime timestamp;
    private String notes;
}
