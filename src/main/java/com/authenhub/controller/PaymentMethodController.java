package com.authenhub.controller;

import com.authenhub.bean.payment.PaymentRequest;
import com.authenhub.bean.payment.PaymentResponse;
import com.authenhub.dto.ApiResponse;
import com.authenhub.dto.PaymentMethodDto;
import com.authenhub.service.interfaces.IPaymentMethodService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payment-methods")
@RequiredArgsConstructor
public class PaymentMethodController {

    private final IPaymentMethodService paymentMethodService;

    @GetMapping
    public ResponseEntity<ApiResponse> getAllPaymentMethods() {
        List<PaymentMethodDto.Response> paymentMethods = paymentMethodService.getAllPaymentMethods();
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Payment methods retrieved successfully")
                .data(paymentMethods)
                .build());
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse> getActivePaymentMethods() {
        List<PaymentMethodDto.Response> paymentMethods = paymentMethodService.getActivePaymentMethods();
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Active payment methods retrieved successfully")
                .data(paymentMethods)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getPaymentMethodById(@PathVariable String id) {
        PaymentMethodDto.Response paymentMethod = paymentMethodService.getPaymentMethodById(id);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Payment method retrieved successfully")
                .data(paymentMethod)
                .build());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('payment:create')")
    public ResponseEntity<ApiResponse> createPaymentMethod(@RequestBody PaymentMethodDto.Request request) {
        PaymentMethodDto.Response paymentMethod = paymentMethodService.createPaymentMethod(request);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Payment method created successfully")
                .data(paymentMethod)
                .build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('payment:update')")
    public ResponseEntity<ApiResponse> updatePaymentMethod(
            @PathVariable String id,
            @RequestBody PaymentMethodDto.Request request) {
        PaymentMethodDto.Response paymentMethod = paymentMethodService.updatePaymentMethod(id, request);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Payment method updated successfully")
                .data(paymentMethod)
                .build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('payment:delete')")
    public ResponseEntity<ApiResponse> deletePaymentMethod(@PathVariable String id) {
        paymentMethodService.deletePaymentMethod(id);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true)
                .message("Payment method deleted successfully")
                .build());
    }

    @PostMapping("/{id}/initialize-payment")
    public ResponseEntity<ApiResponse> initializePayment(
            @PathVariable String id,
            @RequestBody PaymentRequest request) {
        PaymentResponse response = paymentMethodService.initializePayment(id, request);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(response.isSuccess())
                .message(response.getMessage())
                .data(response)
                .build());
    }

    @PostMapping("/{id}/verify-payment")
    public ResponseEntity<ApiResponse> verifyPayment(
            @PathVariable String id,
            @RequestBody String callbackData) {
        boolean isValid = paymentMethodService.verifyPayment(id, callbackData);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(isValid)
                .message(isValid ? "Payment verified successfully" : "Payment verification failed")
                .data(isValid)
                .build());
    }
}
