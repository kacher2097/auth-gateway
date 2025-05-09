package com.authenhub.dto.inventory;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateSalesHistoryRequest {
    
    @NotBlank(message = "SKU is required")
    private String sku;
    
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
    
    @NotNull(message = "Sale date is required")
    private LocalDateTime saleDate;
    
    private Double salePrice;
    
    private String channel;
    
    private String orderId;
    
    private String customerId;
}
