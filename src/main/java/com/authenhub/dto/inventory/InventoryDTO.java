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
public class InventoryDTO {
    private Long id;
    private String sku;
    private String name;
    private Integer quantity;
    private Integer lowStockThreshold;
    private String category;
    private String description;
    private Double costPrice;
    private Double sellingPrice;
    private String supplier;
    private String location;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private Boolean isLowStock;
}
