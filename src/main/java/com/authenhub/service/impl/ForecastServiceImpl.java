package com.authenhub.service.impl;

import com.authenhub.dto.inventory.ForecastDataDTO;
import com.authenhub.dto.inventory.ForecastRequest;
import com.authenhub.entity.ForecastData;
import com.authenhub.entity.Inventory;
import com.authenhub.entity.SalesHistory;
import com.authenhub.exception.ResourceNotFoundException;
import com.authenhub.repository.ForecastDataRepository;
import com.authenhub.repository.InventoryRepository;
import com.authenhub.repository.SalesHistoryRepository;
import com.authenhub.service.ForecastService;
import com.authenhub.utils.TimestampUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ForecastServiceImpl implements ForecastService {

    private final ForecastDataRepository forecastDataRepository;
    private final InventoryRepository inventoryRepository;
    private final SalesHistoryRepository salesHistoryRepository;

    @Override
    public List<ForecastDataDTO> getAllForecasts() {
        log.info("Fetching all forecast data");
        return forecastDataRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ForecastDataDTO getForecastById(Long id) {
        log.info("Fetching forecast data with id: {}", id);
        return forecastDataRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Forecast data not found with id: " + id));
    }

    @Override
    public List<ForecastDataDTO> getForecastsBySku(String sku) {
        log.info("Fetching forecast data for SKU: {}", sku);
        return forecastDataRepository.findBySku(sku).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ForecastDataDTO> getForecastsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Fetching forecast data between {} and {}", startDate, endDate);
        Timestamp start = Timestamp.valueOf(startDate);
        Timestamp end = Timestamp.valueOf(endDate);
        return forecastDataRepository.findForecastsBetweenDates(start, end).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<ForecastDataDTO> generateForecast(ForecastRequest request, String userId) {
        log.info("Generating forecast for SKU: {} from {} to {}", request.getSku(), request.getStartDate(), request.getEndDate());
        
        // Verify that the SKU exists
        Inventory inventory = inventoryRepository.findBySku(request.getSku())
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item not found with SKU: " + request.getSku()));
        
        // Get historical sales data for the SKU
        LocalDateTime historyStartDate = request.getStartDate().minus(90, ChronoUnit.DAYS); // 3 months of history
        Timestamp historyStart = Timestamp.valueOf(historyStartDate);
        Timestamp historyEnd = Timestamp.valueOf(LocalDateTime.now());
        
        List<SalesHistory> salesHistory = salesHistoryRepository.findBySkuAndSaleDateBetween(
                request.getSku(), historyStart, historyEnd);
        
        // If no sales history, we can't make a forecast
        if (salesHistory.isEmpty()) {
            throw new IllegalStateException("No sales history available for SKU: " + request.getSku());
        }
        
        // Group sales by day
        Map<LocalDateTime, Integer> dailySales = new HashMap<>();
        for (SalesHistory sale : salesHistory) {
            LocalDateTime day = sale.getSaleDate().toLocalDateTime().truncatedTo(ChronoUnit.DAYS);
            dailySales.put(day, dailySales.getOrDefault(day, 0) + sale.getQuantity());
        }
        
        // Calculate average daily sales
        double totalSales = salesHistory.stream().mapToInt(SalesHistory::getQuantity).sum();
        double avgDailySales = totalSales / ChronoUnit.DAYS.between(historyStartDate, LocalDateTime.now());
        
        // Generate forecast for each day in the requested range
        List<ForecastData> forecasts = new ArrayList<>();
        LocalDateTime currentDate = request.getStartDate();
        
        while (!currentDate.isAfter(request.getEndDate())) {
            // Simple forecast model: use average daily sales with some randomness
            // In a real implementation, this would use more sophisticated time series forecasting
            double randomFactor = 0.8 + (Math.random() * 0.4); // Random factor between 0.8 and 1.2
            int predictedQuantity = (int) Math.round(avgDailySales * randomFactor);
            
            // Ensure predicted quantity is at least 1 if there were any sales
            if (totalSales > 0 && predictedQuantity < 1) {
                predictedQuantity = 1;
            }
            
            ForecastData forecast = ForecastData.builder()
                    .sku(request.getSku())
                    .forecastDate(Timestamp.valueOf(currentDate))
                    .predictedQuantity(predictedQuantity)
                    .confidenceLevel(0.8) // Fixed confidence level for simplicity
                    .forecastModel(request.getForecastModel() != null ? request.getForecastModel() : "MOVING_AVERAGE")
                    .createdAt(TimestampUtils.now())
                    .createdBy(userId)
                    .build();
            
            forecasts.add(forecast);
            currentDate = currentDate.plus(1, ChronoUnit.DAYS);
        }
        
        // Save all forecasts
        List<ForecastData> savedForecasts = forecastDataRepository.saveAll(forecasts);
        
        // Convert to DTOs and return
        return savedForecasts.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Map<LocalDateTime, Integer> getForecastDataForChart(String sku, LocalDateTime startDate, LocalDateTime endDate) {
        log.info("Fetching forecast chart data for SKU: {} from {} to {}", sku, startDate, endDate);
        
        Timestamp start = Timestamp.valueOf(startDate);
        Timestamp end = Timestamp.valueOf(endDate);
        
        List<ForecastData> forecasts = forecastDataRepository.findBySkuAndForecastDateBetween(sku, start, end);
        
        Map<LocalDateTime, Integer> chartData = new TreeMap<>(); // TreeMap to maintain order by date
        
        for (ForecastData forecast : forecasts) {
            chartData.put(forecast.getForecastDate().toLocalDateTime(), forecast.getPredictedQuantity());
        }
        
        return chartData;
    }
    
    private ForecastDataDTO mapToDTO(ForecastData forecastData) {
        String productName = "";
        
        // Try to get the product name from inventory
        try {
            Inventory inventory = inventoryRepository.findBySku(forecastData.getSku()).orElse(null);
            if (inventory != null) {
                productName = inventory.getName();
            }
        } catch (Exception e) {
            log.warn("Could not fetch product name for SKU: {}", forecastData.getSku(), e);
        }
        
        return ForecastDataDTO.builder()
                .id(forecastData.getId())
                .sku(forecastData.getSku())
                .forecastDate(forecastData.getForecastDate() != null ? forecastData.getForecastDate().toLocalDateTime() : null)
                .predictedQuantity(forecastData.getPredictedQuantity())
                .confidenceLevel(forecastData.getConfidenceLevel())
                .forecastModel(forecastData.getForecastModel())
                .createdAt(forecastData.getCreatedAt() != null ? forecastData.getCreatedAt().toLocalDateTime() : null)
                .updatedAt(forecastData.getUpdatedAt() != null ? forecastData.getUpdatedAt().toLocalDateTime() : null)
                .createdBy(forecastData.getCreatedBy())
                .updatedBy(forecastData.getUpdatedBy())
                .productName(productName)
                .build();
    }
}
