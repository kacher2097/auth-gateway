package com.authenhub.dto.inventory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateInventoryRequest {
    
    private String name;
    
    @Min(value = 0, message = "Quantity must be at least 0")
    private Integer quantity;
    
    private Integer lowStockThreshold;
    
    private String category;
    
    private String description;
    
    private Double costPrice;
    
    private Double sellingPrice;
    
    private String supplier;
    
    private String location;
}
