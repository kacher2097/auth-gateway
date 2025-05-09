package com.authenhub.controller;

import com.authenhub.bean.common.ApiResponse;
import com.authenhub.dto.inventory.CreateSalesHistoryRequest;
import com.authenhub.dto.inventory.SalesHistoryDTO;
import com.authenhub.service.SalesHistoryService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/sales")
@RequiredArgsConstructor
public class SalesHistoryController {

    private final SalesHistoryService salesHistoryService;

    @GetMapping
    public ApiResponse<List<SalesHistoryDTO>> getAllSalesHistory() {
        log.info("REST request to get all sales history");
        List<SalesHistoryDTO> salesHistory = salesHistoryService.getAllSalesHistory();
        return ApiResponse.success(salesHistory);
    }

    @GetMapping("/{id}")
    public ApiResponse<SalesHistoryDTO> getSalesHistoryById(@PathVariable Long id) {
        log.info("REST request to get sales history with id: {}", id);
        SalesHistoryDTO salesHistory = salesHistoryService.getSalesHistoryById(id);
        return ApiResponse.success(salesHistory);
    }

    @GetMapping("/sku/{sku}")
    public ApiResponse<List<SalesHistoryDTO>> getSalesHistoryBySku(@PathVariable String sku) {
        log.info("REST request to get sales history for SKU: {}", sku);
        List<SalesHistoryDTO> salesHistory = salesHistoryService.getSalesHistoryBySku(sku);
        return ApiResponse.success(salesHistory);
    }

    @GetMapping("/date-range")
    public ApiResponse<List<SalesHistoryDTO>> getSalesHistoryByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("REST request to get sales history between {} and {}", startDate, endDate);
        List<SalesHistoryDTO> salesHistory = salesHistoryService.getSalesHistoryByDateRange(startDate, endDate);
        return ApiResponse.success(salesHistory);
    }

    @PostMapping
    public ApiResponse<SalesHistoryDTO> createSalesHistory(
            @Valid @RequestBody CreateSalesHistoryRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("REST request to create sales history for SKU: {}", request.getSku());
        SalesHistoryDTO salesHistory = salesHistoryService.createSalesHistory(request, userDetails.getUsername());
        return ApiResponse.success(salesHistory);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteSalesHistory(@PathVariable Long id) {
        log.info("REST request to delete sales history with id: {}", id);
        salesHistoryService.deleteSalesHistory(id);
        return ApiResponse.success(null);
    }

    @GetMapping("/top-selling")
    public ApiResponse<Map<String, Integer>> getTopSellingProducts(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "10") int limit) {
        log.info("REST request to get top {} selling products between {} and {}", limit, startDate, endDate);
        Map<String, Integer> topProducts = salesHistoryService.getTopSellingProducts(startDate, endDate, limit);
        return ApiResponse.success(topProducts);
    }
}
