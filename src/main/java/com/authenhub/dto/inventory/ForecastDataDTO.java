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
public class ForecastDataDTO {
    private Long id;
    private String sku;
    private LocalDateTime forecastDate;
    private Integer predictedQuantity;
    private Double confidenceLevel;
    private String forecastModel;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String createdBy;
    private String updatedBy;
    private String productName; // Added for convenience when displaying forecasts
}
