package com.authenhub.controller;

import com.authenhub.bean.DateRangeRequest;
import com.authenhub.bean.PaginatedDateRangeRequest;
import com.authenhub.dto.ApiResponse;
import com.authenhub.dto.PaymentTransactionDTO;
import com.authenhub.service.PaymentTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentTransactionController {

    private final PaymentTransactionService paymentTransactionService;

    @GetMapping
    public ResponseEntity<ApiResponse> findTransactions(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) Long amount,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDateStr,
            @RequestParam(required = false) String endDateStr) {

        // Convert string dates to Timestamp
        Timestamp startDate = startDateStr != null ? Timestamp.valueOf(startDateStr.replace('T', ' ').substring(0, 19)) : null;
        Timestamp endDate = endDateStr != null ? Timestamp.valueOf(endDateStr.replace('T', ' ').substring(0, 19)) : null;

        List<PaymentTransactionDTO> transactions = paymentTransactionService.findTransactions(
                userId, amount, status, startDate, endDate);

        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Transactions retrieved successfully")
                .data(transactions)
                .build());
    }

    @GetMapping("/stats/by-status")
    public ResponseEntity<ApiResponse> getTransactionStatsByStatus(
            @RequestParam(required = false) String startDateStr,
            @RequestParam(required = false) String endDateStr) {

        // Convert string dates to Timestamp
        Timestamp startDate = startDateStr != null ? Timestamp.valueOf(startDateStr.replace('T', ' ').substring(0, 19)) : null;
        Timestamp endDate = endDateStr != null ? Timestamp.valueOf(endDateStr.replace('T', ' ').substring(0, 19)) : null;

        List<PaymentTransactionDTO> stats = paymentTransactionService.getTransactionStatsByStatus(startDate, endDate);

        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Transaction statistics retrieved successfully")
                .data(stats)
                .build());
    }

    @GetMapping("/stats/by-user")
    public ResponseEntity<ApiResponse> getPaginatedTransactionStatsByUser(
            @RequestParam(required = false) String startDateStr,
            @RequestParam(required = false) String endDateStr,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // Convert string dates to Timestamp
        Timestamp startDate = startDateStr != null ? Timestamp.valueOf(startDateStr.replace('T', ' ').substring(0, 19)) : null;
        Timestamp endDate = endDateStr != null ? Timestamp.valueOf(endDateStr.replace('T', ' ').substring(0, 19)) : null;

        List<PaymentTransactionDTO> stats = paymentTransactionService.getPaginatedTransactionStatsByUser(
                startDate, endDate, page, size);

        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Transaction statistics by user retrieved successfully")
                .data(stats)
                .build());
    }

    @PostMapping("/stats/by-status")
    public ResponseEntity<ApiResponse> getTransactionStatsByStatusPost(
            @RequestBody(required = false) DateRangeRequest request) {

        Timestamp startDate = request != null ? request.getStartDate() : null;
        Timestamp endDate = request != null ? request.getEndDate() : null;

        List<PaymentTransactionDTO> stats = paymentTransactionService.getTransactionStatsByStatus(startDate, endDate);

        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Transaction statistics retrieved successfully")
                .data(stats)
                .build());
    }

    @PostMapping("/stats/by-user")
    public ResponseEntity<ApiResponse> getPaginatedTransactionStatsByUserPost(
            @RequestBody PaginatedDateRangeRequest request) {

        Timestamp startDate = request != null ? request.getStartDate() : null;
        Timestamp endDate = request != null ? request.getEndDate() : null;
        int page = request != null && request.getPage() != null ? request.getPage() : 0;
        int size = request != null && request.getSize() != null ? request.getSize() : 10;

        List<PaymentTransactionDTO> stats = paymentTransactionService.getPaginatedTransactionStatsByUser(
                startDate, endDate, page, size);

        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Transaction statistics by user retrieved successfully")
                .data(stats)
                .build());
    }
}
