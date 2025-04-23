package com.authenhub.controller;

import com.authenhub.bean.payment.PaymentRequest;
import com.authenhub.bean.payment.PaymentResponse;
import com.authenhub.bean.common.ApiResponse;
import com.authenhub.dto.PaymentMethodDto;
import com.authenhub.service.interfaces.IPaymentMethodService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment-methods")
@RequiredArgsConstructor
public class PaymentMethodController {

    private final IPaymentMethodService paymentMethodService;

    @GetMapping
    public ApiResponse<?> getAllPaymentMethods() {
        List<PaymentMethodDto.Response> paymentMethods = paymentMethodService.getAllPaymentMethods();
        return ApiResponse.success(paymentMethods);
    }

    @GetMapping("/active")
    public ApiResponse<?> getActivePaymentMethods() {
        List<PaymentMethodDto.Response> paymentMethods = paymentMethodService.getActivePaymentMethods();
        return ApiResponse.success(paymentMethods);
    }

    @GetMapping("/{id}")
    public ApiResponse<?> getPaymentMethodById(@PathVariable String id) {
        PaymentMethodDto.Response paymentMethod = paymentMethodService.getPaymentMethodById(id);
        return ApiResponse.success(paymentMethod);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('payment:create')")
    public ApiResponse<?> createPaymentMethod(@RequestBody PaymentMethodDto.Request request) {
        PaymentMethodDto.Response paymentMethod = paymentMethodService.createPaymentMethod(request);
        return ApiResponse.success(paymentMethod);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('payment:update')")
    public ApiResponse<?> updatePaymentMethod(
            @PathVariable String id,
            @RequestBody PaymentMethodDto.Request request) {
        PaymentMethodDto.Response paymentMethod = paymentMethodService.updatePaymentMethod(id, request);
        return ApiResponse.success(paymentMethod);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('payment:delete')")
    public ApiResponse<?> deletePaymentMethod(@PathVariable String id) {
        paymentMethodService.deletePaymentMethod(id);
        return ApiResponse.success(null);
    }

    @PostMapping("/{id}/initialize-payment")
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
