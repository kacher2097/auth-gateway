package com.authenhub.dto.inventory;

import jakarta.validation.constraints.NotBlank;
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
public class CreateInventoryRequest {
    
    @NotBlank(message = "SKU is required")
    private String sku;
    
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotNull(message = "Quantity is required")
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
