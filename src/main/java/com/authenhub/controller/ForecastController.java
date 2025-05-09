package com.authenhub.controller;

import com.authenhub.bean.common.ApiResponse;
import com.authenhub.dto.inventory.ForecastDataDTO;
import com.authenhub.dto.inventory.ForecastRequest;
import com.authenhub.service.ForecastService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/forecast")
@RequiredArgsConstructor
public class ForecastController {

    private final ForecastService forecastService;

    @GetMapping
    public ApiResponse<List<ForecastDataDTO>> getAllForecasts() {
        log.info("REST request to get all forecast data");
        List<ForecastDataDTO> forecasts = forecastService.getAllForecasts();
        return ApiResponse.success(forecasts);
    }

    @GetMapping("/{id}")
    public ApiResponse<ForecastDataDTO> getForecastById(@PathVariable Long id) {
        log.info("REST request to get forecast data with id: {}", id);
        ForecastDataDTO forecast = forecastService.getForecastById(id);
        return ApiResponse.success(forecast);
    }

    @GetMapping("/sku/{sku}")
    public ApiResponse<List<ForecastDataDTO>> getForecastsBySku(@PathVariable String sku) {
        log.info("REST request to get forecast data for SKU: {}", sku);
        List<ForecastDataDTO> forecasts = forecastService.getForecastsBySku(sku);
        return ApiResponse.success(forecasts);
    }

    @GetMapping("/date-range")
    public ApiResponse<List<ForecastDataDTO>> getForecastsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("REST request to get forecast data between {} and {}", startDate, endDate);
        List<ForecastDataDTO> forecasts = forecastService.getForecastsByDateRange(startDate, endDate);
        return ApiResponse.success(forecasts);
    }

    @PostMapping("/generate")
    public ApiResponse<List<ForecastDataDTO>> generateForecast(
            @Valid @RequestBody ForecastRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("REST request to generate forecast for SKU: {}", request.getSku());
        List<ForecastDataDTO> forecasts = forecastService.generateForecast(request, userDetails.getUsername());
        return ApiResponse.success(forecasts);
    }

    @GetMapping("/chart/{sku}")
    public ApiResponse<Map<LocalDateTime, Integer>> getForecastDataForChart(
            @PathVariable String sku,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("REST request to get forecast chart data for SKU: {}", sku);
        Map<LocalDateTime, Integer> chartData = forecastService.getForecastDataForChart(sku, startDate, endDate);
        return ApiResponse.success(chartData);
    }
}
