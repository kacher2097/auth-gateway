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
public class ReplenishmentSuggestionDTO {
    private Long id;
    private String sku;
    private Integer suggestedQuantity;
    private String reason;
    private String status;
    private Integer approvedQuantity;
    private String approvedBy;
    private LocalDateTime approvedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private String productName; // Added for convenience when displaying suggestions
    private Integer currentStock; // Added for convenience when displaying suggestions
}
