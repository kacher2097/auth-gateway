//package com.authenhub.controller;
//
//import com.authenhub.bean.common.ApiResponse;
//import com.authenhub.dto.inventory.ApproveReplenishmentRequest;
//import com.authenhub.dto.inventory.ReplenishmentSuggestionDTO;
//import com.authenhub.service.ReplenishmentService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.web.bind.annotation.*;
//
//import javax.validation.Valid;
//import java.util.List;
//
//@Slf4j
//@RestController
//@RequestMapping("/api/v1/replenishment")
//@RequiredArgsConstructor
//public class ReplenishmentController {
//
//    private final ReplenishmentService replenishmentService;
//
//    @GetMapping
//    public ApiResponse<List<ReplenishmentSuggestionDTO>> getAllReplenishmentSuggestions() {
//        log.info("REST request to get all replenishment suggestions");
//        List<ReplenishmentSuggestionDTO> suggestions = replenishmentService.getAllReplenishmentSuggestions();
//        return ApiResponse.success(suggestions);
//    }
//
//    @GetMapping("/{id}")
//    public ApiResponse<ReplenishmentSuggestionDTO> getReplenishmentSuggestionById(@PathVariable Long id) {
//        log.info("REST request to get replenishment suggestion with id: {}", id);
//        ReplenishmentSuggestionDTO suggestion = replenishmentService.getReplenishmentSuggestionById(id);
//        return ApiResponse.success(suggestion);
//    }
//
//    @GetMapping("/sku/{sku}")
//    public ApiResponse<List<ReplenishmentSuggestionDTO>> getReplenishmentSuggestionsBySku(@PathVariable String sku) {
//        log.info("REST request to get replenishment suggestions for SKU: {}", sku);
//        List<ReplenishmentSuggestionDTO> suggestions = replenishmentService.getReplenishmentSuggestionsBySku(sku);
//        return ApiResponse.success(suggestions);
//    }
//
//    @GetMapping("/status/{status}")
//    public ApiResponse<List<ReplenishmentSuggestionDTO>> getReplenishmentSuggestionsByStatus(@PathVariable String status) {
//        log.info("REST request to get replenishment suggestions with status: {}", status);
//        List<ReplenishmentSuggestionDTO> suggestions = replenishmentService.getReplenishmentSuggestionsByStatus(status);
//        return ApiResponse.success(suggestions);
//    }
//
//    @PostMapping("/generate")
//    public ApiResponse<List<ReplenishmentSuggestionDTO>> generateReplenishmentSuggestions(
//            @AuthenticationPrincipal UserDetails userDetails) {
//        log.info("REST request to generate replenishment suggestions");
//        List<ReplenishmentSuggestionDTO> suggestions = replenishmentService.generateReplenishmentSuggestions(userDetails.getUsername());
//        return ApiResponse.success(suggestions);
//    }
//
//    @PutMapping("/{id}/approve")
//    public ApiResponse<ReplenishmentSuggestionDTO> approveReplenishmentSuggestion(
//            @PathVariable Long id,
//            @Valid @RequestBody ApproveReplenishmentRequest request,
//            @AuthenticationPrincipal UserDetails userDetails) {
//        log.info("REST request to approve replenishment suggestion with id: {}", id);
//        ReplenishmentSuggestionDTO suggestion = replenishmentService.approveReplenishmentSuggestion(id, request, userDetails.getUsername());
//        return ApiResponse.success(suggestion);
//    }
//
//    @PutMapping("/{id}/reject")
//    public ApiResponse<ReplenishmentSuggestionDTO> rejectReplenishmentSuggestion(
//            @PathVariable Long id,
//            @AuthenticationPrincipal UserDetails userDetails) {
//        log.info("REST request to reject replenishment suggestion with id: {}", id);
//        ReplenishmentSuggestionDTO suggestion = replenishmentService.rejectReplenishmentSuggestion(id, userDetails.getUsername());
//        return ApiResponse.success(suggestion);
//    }
//}
