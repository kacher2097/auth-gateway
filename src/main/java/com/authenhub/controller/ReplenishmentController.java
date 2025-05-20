package com.authenhub.controller;

import com.authenhub.bean.common.ApiResponse;
import com.authenhub.dto.inventory.ApproveReplenishmentRequest;
import com.authenhub.dto.inventory.ReplenishmentSuggestionDTO;
import com.authenhub.service.ReplenishmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/replenishment")
@RequiredArgsConstructor
@Tag(name = "Replenishment", description = "API để quản lý và tự động hóa việc bổ sung hàng tồn kho")
public class ReplenishmentController {

    private final ReplenishmentService replenishmentService;

    @GetMapping
    @Operation(summary = "Lấy tất cả đề xuất bổ sung hàng",
            description = "Lấy danh sách tất cả các đề xuất bổ sung hàng tồn kho.")
    public ApiResponse<List<ReplenishmentSuggestionDTO>> getAllReplenishmentSuggestions() {
        log.info("REST request to get all replenishment suggestions");
        List<ReplenishmentSuggestionDTO> suggestions = replenishmentService.getAllReplenishmentSuggestions();
        return ApiResponse.success(suggestions);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy đề xuất bổ sung hàng theo ID",
            description = "Lấy thông tin chi tiết của một đề xuất bổ sung hàng dựa trên ID.")
    public ApiResponse<ReplenishmentSuggestionDTO> getReplenishmentSuggestionById(@PathVariable Long id) {
        log.info("REST request to get replenishment suggestion with id: {}", id);
        ReplenishmentSuggestionDTO suggestion = replenishmentService.getReplenishmentSuggestionById(id);
        return ApiResponse.success(suggestion);
    }

    @GetMapping("/sku/{sku}")
    @Operation(summary = "Lấy đề xuất bổ sung hàng theo SKU",
            description = "Lấy danh sách các đề xuất bổ sung hàng cho một mặt hàng dựa trên mã SKU.")
    public ApiResponse<List<ReplenishmentSuggestionDTO>> getReplenishmentSuggestionsBySku(@PathVariable String sku) {
        log.info("REST request to get replenishment suggestions for SKU: {}", sku);
        List<ReplenishmentSuggestionDTO> suggestions = replenishmentService.getReplenishmentSuggestionsBySku(sku);
        return ApiResponse.success(suggestions);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Lấy đề xuất bổ sung hàng theo trạng thái",
            description = "Lấy danh sách các đề xuất bổ sung hàng dựa trên trạng thái (mới, đã duyệt, đã từ chối).")
    public ApiResponse<List<ReplenishmentSuggestionDTO>> getReplenishmentSuggestionsByStatus(@PathVariable String status) {
        log.info("REST request to get replenishment suggestions with status: {}", status);
        List<ReplenishmentSuggestionDTO> suggestions = replenishmentService.getReplenishmentSuggestionsByStatus(status);
        return ApiResponse.success(suggestions);
    }

    @PostMapping("/generate")
    @Operation(summary = "Tạo đề xuất bổ sung hàng mới",
            description = "Tạo các đề xuất bổ sung hàng mới dựa trên dữ liệu bán hàng và dự báo.")
    public ApiResponse<List<ReplenishmentSuggestionDTO>> generateReplenishmentSuggestions(
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("REST request to generate replenishment suggestions");
        List<ReplenishmentSuggestionDTO> suggestions = replenishmentService.generateReplenishmentSuggestions(userDetails.getUsername());
        return ApiResponse.success(suggestions);
    }

    @PutMapping("/{id}/approve")
    @Operation(summary = "Phê duyệt đề xuất bổ sung hàng",
            description = "Phê duyệt một đề xuất bổ sung hàng và cập nhật số lượng hàng tồn kho.")
    public ApiResponse<ReplenishmentSuggestionDTO> approveReplenishmentSuggestion(
            @PathVariable Long id,
            @Valid @RequestBody ApproveReplenishmentRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("REST request to approve replenishment suggestion with id: {}", id);
        ReplenishmentSuggestionDTO suggestion = replenishmentService.approveReplenishmentSuggestion(id, request, userDetails.getUsername());
        return ApiResponse.success(suggestion);
    }

    @PutMapping("/{id}/reject")
    @Operation(summary = "Từ chối đề xuất bổ sung hàng",
            description = "Từ chối một đề xuất bổ sung hàng và đánh dấu nó là đã bị từ chối.")
    public ApiResponse<ReplenishmentSuggestionDTO> rejectReplenishmentSuggestion(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("REST request to reject replenishment suggestion with id: {}", id);
        ReplenishmentSuggestionDTO suggestion = replenishmentService.rejectReplenishmentSuggestion(id, userDetails.getUsername());
        return ApiResponse.success(suggestion);
    }
}
