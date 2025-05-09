package com.authenhub.service;

import com.authenhub.dto.inventory.ForecastDataDTO;
import com.authenhub.dto.inventory.ForecastRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface ForecastService {
    
    List<ForecastDataDTO> getAllForecasts();
    
    ForecastDataDTO getForecastById(Long id);
    
    List<ForecastDataDTO> getForecastsBySku(String sku);
    
    List<ForecastDataDTO> getForecastsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    List<ForecastDataDTO> generateForecast(ForecastRequest request, String userId);
    
    Map<LocalDateTime, Integer> getForecastDataForChart(String sku, LocalDateTime startDate, LocalDateTime endDate);
}
