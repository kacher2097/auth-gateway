package com.authenhub.controller;

import com.authenhub.bean.common.ApiResponse;
import com.authenhub.dto.inventory.CreateSalesHistoryRequest;
import com.authenhub.dto.inventory.SalesHistoryDTO;
import com.authenhub.service.SalesHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Sales History", description = "API để quản lý lịch sử bán hàng và phân tích doanh số")
public class SalesHistoryController {

    private final SalesHistoryService salesHistoryService;

    @GetMapping
    @Operation(summary = "Lấy tất cả lịch sử bán hàng",
            description = "Lấy danh sách tất cả các giao dịch bán hàng trong hệ thống.")
    public ApiResponse<List<SalesHistoryDTO>> getAllSalesHistory() {
        log.info("REST request to get all sales history");
        List<SalesHistoryDTO> salesHistory = salesHistoryService.getAllSalesHistory();
        return ApiResponse.success(salesHistory);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy lịch sử bán hàng theo ID",
            description = "Lấy thông tin chi tiết của một giao dịch bán hàng dựa trên ID.")
    public ApiResponse<SalesHistoryDTO> getSalesHistoryById(@PathVariable Long id) {
        log.info("REST request to get sales history with id: {}", id);
        SalesHistoryDTO salesHistory = salesHistoryService.getSalesHistoryById(id);
        return ApiResponse.success(salesHistory);
    }

    @GetMapping("/sku/{sku}")
    @Operation(summary = "Lấy lịch sử bán hàng theo SKU",
            description = "Lấy danh sách các giao dịch bán hàng cho một mặt hàng dựa trên mã SKU.")
    public ApiResponse<List<SalesHistoryDTO>> getSalesHistoryBySku(@PathVariable String sku) {
        log.info("REST request to get sales history for SKU: {}", sku);
        List<SalesHistoryDTO> salesHistory = salesHistoryService.getSalesHistoryBySku(sku);
        return ApiResponse.success(salesHistory);
    }

    @GetMapping("/date-range")
    @Operation(summary = "Lấy lịch sử bán hàng theo khoảng thời gian",
            description = "Lấy danh sách các giao dịch bán hàng trong một khoảng thời gian cụ thể.")
    public ApiResponse<List<SalesHistoryDTO>> getSalesHistoryByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        log.info("REST request to get sales history between {} and {}", startDate, endDate);
        List<SalesHistoryDTO> salesHistory = salesHistoryService.getSalesHistoryByDateRange(startDate, endDate);
        return ApiResponse.success(salesHistory);
    }

    @PostMapping
    @Operation(summary = "Tạo giao dịch bán hàng mới",
            description = "Tạo một giao dịch bán hàng mới trong hệ thống.")
    public ApiResponse<SalesHistoryDTO> createSalesHistory(
            @Valid @RequestBody CreateSalesHistoryRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("REST request to create sales history for SKU: {}", request.getSku());
        SalesHistoryDTO salesHistory = salesHistoryService.createSalesHistory(request, userDetails.getUsername());
        return ApiResponse.success(salesHistory);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa giao dịch bán hàng",
            description = "Xóa một giao dịch bán hàng dựa trên ID.")
    public ApiResponse<Void> deleteSalesHistory(@PathVariable Long id) {
        log.info("REST request to delete sales history with id: {}", id);
        salesHistoryService.deleteSalesHistory(id);
        return ApiResponse.success(null);
    }

    @GetMapping("/top-selling")
    @Operation(summary = "Lấy sản phẩm bán chạy nhất",
            description = "Lấy danh sách các sản phẩm bán chạy nhất trong một khoảng thời gian cụ thể.")
    public ApiResponse<Map<String, Integer>> getTopSellingProducts(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "10") int limit) {
        log.info("REST request to get top {} selling products between {} and {}", limit, startDate, endDate);
        Map<String, Integer> topProducts = salesHistoryService.getTopSellingProducts(startDate, endDate, limit);
        return ApiResponse.success(topProducts);
    }
}
