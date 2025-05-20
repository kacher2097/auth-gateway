package com.authenhub.controller;

import com.authenhub.bean.common.ApiResponse;
import com.authenhub.bean.payment.PaymentMethodRequest;
import com.authenhub.bean.payment.PaymentMethodResponse;
import com.authenhub.bean.payment.PaymentRequest;
import com.authenhub.bean.payment.PaymentResponse;
import com.authenhub.service.interfaces.IPaymentMethodService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment-methods")
@RequiredArgsConstructor
@Tag(name = "Payment Methods", description = "API để quản lý các phương thức thanh toán")
public class PaymentMethodController {

    private final IPaymentMethodService paymentMethodService;

    @GetMapping
    @Operation(summary = "Lấy tất cả phương thức thanh toán",
            description = "Lấy danh sách tất cả các phương thức thanh toán trong hệ thống.")
    public ApiResponse<?> getAllPaymentMethods() {
        List<PaymentMethodResponse> paymentMethods = paymentMethodService.getAllPaymentMethods();
        return ApiResponse.success(paymentMethods);
    }

    @GetMapping("/active")
    @Operation(summary = "Lấy các phương thức thanh toán đang hoạt động",
            description = "Lấy danh sách các phương thức thanh toán đang hoạt động trong hệ thống.")
    public ApiResponse<?> getActivePaymentMethods() {
        List<PaymentMethodResponse> paymentMethods = paymentMethodService.getActivePaymentMethods();
        return ApiResponse.success(paymentMethods);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy phương thức thanh toán theo ID",
            description = "Lấy thông tin chi tiết của một phương thức thanh toán dựa trên ID.")
    public ApiResponse<?> getPaymentMethodById(@PathVariable String id) {
        PaymentMethodResponse paymentMethod = paymentMethodService.getPaymentMethodById(id);
        return ApiResponse.success(paymentMethod);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('payment:create')")
    @Operation(summary = "Tạo phương thức thanh toán mới",
            description = "Tạo một phương thức thanh toán mới trong hệ thống.")
    public ApiResponse<?> createPaymentMethod(@RequestBody PaymentMethodRequest request) {
        PaymentMethodResponse paymentMethod = paymentMethodService.createPaymentMethod(request);
        return ApiResponse.success(paymentMethod);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('payment:update')")
    @Operation(summary = "Cập nhật phương thức thanh toán",
            description = "Cập nhật thông tin của một phương thức thanh toán dựa trên ID.")
    public ApiResponse<?> updatePaymentMethod(
            @PathVariable String id,
            @RequestBody PaymentMethodRequest request) {
        PaymentMethodResponse paymentMethod = paymentMethodService.updatePaymentMethod(id, request);
        return ApiResponse.success(paymentMethod);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('payment:delete')")
    @Operation(summary = "Xóa phương thức thanh toán",
            description = "Xóa một phương thức thanh toán dựa trên ID.")
    public ApiResponse<?> deletePaymentMethod(@PathVariable String id) {
        paymentMethodService.deletePaymentMethod(id);
        return ApiResponse.success(null);
    }

    @PostMapping("/{id}/initialize-payment")
    @Operation(summary = "Khởi tạo thanh toán",
            description = "Khởi tạo một giao dịch thanh toán mới với phương thức thanh toán được chỉ định.")
    public ApiResponse<?> initializePayment(
            @PathVariable String id,
            @RequestBody PaymentRequest request) {
        PaymentResponse response = paymentMethodService.initializePayment(id, request);
        if (response.isSuccess()) {
            return ApiResponse.success(response);
        } else {
            return ApiResponse.error("PAYMENT_ERROR", response.getMessage());
        }
    }

    @PostMapping("/{id}/verify-payment")
    @Operation(summary = "Xác minh thanh toán",
            description = "Xác minh tính hợp lệ của một giao dịch thanh toán dựa trên dữ liệu callback.")
    public ApiResponse<?> verifyPayment(
            @PathVariable String id,
            @RequestBody String callbackData) {
        boolean isValid = paymentMethodService.verifyPayment(id, callbackData);
        if (isValid) {
            return ApiResponse.success(true);
        } else {
            return ApiResponse.error("VERIFICATION_FAILED", "Payment verification failed");
        }
    }
}
