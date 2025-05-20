package com.authenhub.controller;

import com.authenhub.bean.common.ApiResponse;
import com.authenhub.dto.inventory.ForecastDataDTO;
import com.authenhub.dto.inventory.ForecastRequest;
import com.authenhub.service.ForecastService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Forecast", description = "API để dự báo nhu cầu và quản lý hàng tồn kho")
public class ForecastController {

    private final ForecastService forecastService;

    @GetMapping
    @Operation(summary = "Lấy tất cả dự báo",
            description = "Lấy danh sách tất cả các dự báo trong hệ thống.")
    public ApiResponse<List<ForecastDataDTO>> getAllForecasts() {
        log.info("REST request to get all forecast data");
        List<ForecastDataDTO> forecasts = forecastService.getAllForecasts();
        return ApiResponse.success(forecasts);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy dự báo theo ID",
            description = "Lấy thông tin chi tiết của một dự báo dựa trên ID.")
    public ApiResponse<ForecastDataDTO> getForecastById(@PathVariable Long id) {
        log.info("REST request to get forecast data with id: {}", id);
        ForecastDataDTO forecast = forecastService.getForecastById(id);
        return ApiResponse.success(forecast);
    }

    @GetMapping("/sku/{sku}")
    @Operation(summary = "Lấy dự báo theo SKU",
            description = "Lấy danh sách các dự báo cho một mặt hàng dựa trên mã SKU.")
    public ApiResponse<List<ForecastDataDTO>> getForecastsBySku(@PathVariable String sku) {
        log.info("REST request to get forecast data for SKU: {}", sku);
        List<ForecastDataDTO> forecasts = forecastService.getForecastsBySku(sku);
        return ApiResponse.success(forecasts);
    }

    @GetMapping("/date-range")
    @Operation(summary = "Lấy dự báo theo khoảng thời gian",
            description = "Lấy danh sách các dự báo trong một khoảng thời gian cụ thể.")
    public ApiResponse<List<ForecastDataDTO>> getForecastsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("REST request to get forecast data between {} and {}", startDate, endDate);
        List<ForecastDataDTO> forecasts = forecastService.getForecastsByDateRange(startDate, endDate);
        return ApiResponse.success(forecasts);
    }

    @PostMapping("/generate")
    @Operation(summary = "Tạo dự báo mới",
            description = "Tạo dự báo mới cho một mặt hàng dựa trên các tham số đầu vào.")
    public ApiResponse<List<ForecastDataDTO>> generateForecast(
            @Valid @RequestBody ForecastRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("REST request to generate forecast for SKU: {}", request.getSku());
        List<ForecastDataDTO> forecasts = forecastService.generateForecast(request, userDetails.getUsername());
        return ApiResponse.success(forecasts);
    }

    @GetMapping("/chart/{sku}")
    @Operation(summary = "Lấy dữ liệu biểu đồ dự báo",
            description = "Lấy dữ liệu dự báo để hiển thị trên biểu đồ cho một mặt hàng cụ thể.")
    public ApiResponse<Map<LocalDateTime, Integer>> getForecastDataForChart(
            @PathVariable String sku,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("REST request to get forecast chart data for SKU: {}", sku);
        Map<LocalDateTime, Integer> chartData = forecastService.getForecastDataForChart(sku, startDate, endDate);
        return ApiResponse.success(chartData);
    }
}
