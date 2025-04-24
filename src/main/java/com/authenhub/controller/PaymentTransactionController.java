package com.authenhub.controller;

import com.authenhub.bean.DateRangeRequest;
import com.authenhub.bean.PaginatedDateRangeRequest;
import com.authenhub.bean.common.ApiResponse;
import com.authenhub.bean.payment.PaymentTransactionDTO;
import com.authenhub.service.PaymentTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentTransactionController {

    private final PaymentTransactionService paymentTransactionService;

    @GetMapping
    public ApiResponse<?> findTransactions(
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

        return ApiResponse.success(transactions);
    }

    @GetMapping("/stats/by-status")
    public ApiResponse<?> getTransactionStatsByStatus(
            @RequestParam(required = false) String startDateStr,
            @RequestParam(required = false) String endDateStr) {

        // Convert string dates to Timestamp
        Timestamp startDate = startDateStr != null ? Timestamp.valueOf(startDateStr.replace('T', ' ').substring(0, 19)) : null;
        Timestamp endDate = endDateStr != null ? Timestamp.valueOf(endDateStr.replace('T', ' ').substring(0, 19)) : null;

        List<PaymentTransactionDTO> stats = paymentTransactionService.getTransactionStatsByStatus(startDate, endDate);

        return ApiResponse.success(stats);
    }

    @GetMapping("/stats/by-user")
    public ApiResponse<?> getPaginatedTransactionStatsByUser(
            @RequestParam(required = false) String startDateStr,
            @RequestParam(required = false) String endDateStr,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        // Convert string dates to Timestamp
        Timestamp startDate = startDateStr != null ? Timestamp.valueOf(startDateStr.replace('T', ' ').substring(0, 19)) : null;
        Timestamp endDate = endDateStr != null ? Timestamp.valueOf(endDateStr.replace('T', ' ').substring(0, 19)) : null;

        List<PaymentTransactionDTO> stats = paymentTransactionService.getPaginatedTransactionStatsByUser(
                startDate, endDate, page, size);

        return ApiResponse.success(stats);
    }

    @PostMapping("/stats/by-status")
    public ApiResponse<?> getTransactionStatsByStatusPost(
            @RequestBody(required = false) DateRangeRequest request) {

        Timestamp startDate = request != null ? request.getStartDate() : null;
        Timestamp endDate = request != null ? request.getEndDate() : null;

        List<PaymentTransactionDTO> stats = paymentTransactionService.getTransactionStatsByStatus(startDate, endDate);

        return ApiResponse.success(stats);
    }

    @PostMapping("/stats/by-user")
    public ApiResponse<?> getPaginatedTransactionStatsByUserPost(
            @RequestBody PaginatedDateRangeRequest request) {

        Timestamp startDate = request != null ? request.getStartDate() : null;
        Timestamp endDate = request != null ? request.getEndDate() : null;
        int page = request != null && request.getPage() != null ? request.getPage() : 0;
        int size = request != null && request.getSize() != null ? request.getSize() : 10;

        List<PaymentTransactionDTO> stats = paymentTransactionService.getPaginatedTransactionStatsByUser(
                startDate, endDate, page, size);
        return ApiResponse.success(stats);
    }
}
