package com.authenhub.dto.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApproveReplenishmentRequest {
    
    @NotNull(message = "Approved quantity is required")
    @Min(value = 0, message = "Approved quantity must be at least 0")
    private Integer approvedQuantity;
    
    private String notes;
}
