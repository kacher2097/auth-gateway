package com.authenhub.service.payment;

import com.authenhub.bean.payment.PaymentRequest;
import com.authenhub.bean.payment.PaymentResponse;
import com.authenhub.dto.PaymentMethodDto;
import com.authenhub.entity.PaymentMethod;
import com.authenhub.repository.PaymentMethodRepository;
import com.authenhub.service.payment.provider.PaymentProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PaymentMethodServiceTest {

    @Mock
    private PaymentMethodRepository paymentMethodRepository;

    @Mock
    private Map<String, PaymentProvider> paymentProviders;

    @Mock
    private PaymentProvider vnpayProvider;

    @InjectMocks
    private PaymentMethodService paymentMethodService;

    private PaymentMethod vnpayMethod;
    private PaymentMethodDto.Request vnpayRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup payment providers
        when(paymentProviders.containsKey("VNPAY")).thenReturn(true);
        when(paymentProviders.get("VNPAY")).thenReturn(vnpayProvider);
        when(vnpayProvider.getProviderName()).thenReturn("VNPAY");
        when(vnpayProvider.isAvailable()).thenReturn(true);

        // Setup payment method
        vnpayMethod = new PaymentMethod();
        vnpayMethod.setId("1");
        vnpayMethod.setName("VNPay");
        vnpayMethod.setDisplayName("VN Pay");
        vnpayMethod.setDescription("Payment with VNPay");
        vnpayMethod.setProviderType("VNPAY");
        vnpayMethod.setActive(true);
        vnpayMethod.setConfig(new HashMap<>());
        vnpayMethod.setIconUrl("https://example.com/vnpay.png");
        vnpayMethod.setFeePercentage(1.5);
        vnpayMethod.setFixedFee(1000);
        vnpayMethod.setCurrency("VND");

        // Setup payment method request
        vnpayRequest = new PaymentMethodDto.Request();
        vnpayRequest.setName("VNPay");
        vnpayRequest.setDisplayName("VN Pay");
        vnpayRequest.setDescription("Payment with VNPay");
        vnpayRequest.setProviderType("VNPAY");
        vnpayRequest.setActive(true);
        vnpayRequest.setConfig(new HashMap<>());
        vnpayRequest.setIconUrl("https://example.com/vnpay.png");
        vnpayRequest.setFeePercentage(1.5);
        vnpayRequest.setFixedFee(1000);
        vnpayRequest.setCurrency("VND");
    }

    @Test
    void getAllPaymentMethods() {
        // Setup
        when(paymentMethodRepository.findAll()).thenReturn(Arrays.asList(vnpayMethod));

        // Execute
        List<PaymentMethodDto.Response> result = paymentMethodService.getAllPaymentMethods();

        // Verify
        assertEquals(1, result.size());
        assertEquals("VNPay", result.get(0).getName());
        assertEquals("VNPAY", result.get(0).getProviderType());
        verify(paymentMethodRepository).findAll();
    }

    @Test
    void getActivePaymentMethods() {
        // Setup
        when(paymentMethodRepository.findByIsActiveTrue()).thenReturn(Arrays.asList(vnpayMethod));

        // Execute
        List<PaymentMethodDto.Response> result = paymentMethodService.getActivePaymentMethods();

        // Verify
        assertEquals(1, result.size());
        assertEquals("VNPay", result.get(0).getName());
        assertTrue(result.get(0).isActive());
        verify(paymentMethodRepository).findByIsActiveTrue();
    }

    @Test
    void getPaymentMethodById() {
        // Setup
        when(paymentMethodRepository.findById("1")).thenReturn(Optional.of(vnpayMethod));

        // Execute
        PaymentMethodDto.Response result = paymentMethodService.getPaymentMethodById("1");

        // Verify
        assertEquals("VNPay", result.getName());
        assertEquals("VNPAY", result.getProviderType());
        verify(paymentMethodRepository).findById("1");
    }

    @Test
    void getPaymentMethodById_NotFound() {
        // Setup
        when(paymentMethodRepository.findById("999")).thenReturn(Optional.empty());

        // Execute & Verify
        assertThrows(ResponseStatusException.class, () -> {
            paymentMethodService.getPaymentMethodById("999");
        });
        verify(paymentMethodRepository).findById("999");
    }

    @Test
    void createPaymentMethod() {
        // Setup
        when(paymentMethodRepository.existsByName("VNPay")).thenReturn(false);
        when(paymentMethodRepository.save(any(PaymentMethod.class))).thenReturn(vnpayMethod);

        // Execute
        PaymentMethodDto.Response result = paymentMethodService.createPaymentMethod(vnpayRequest);

        // Verify
        assertEquals("VNPay", result.getName());
        assertEquals("VNPAY", result.getProviderType());
        verify(paymentMethodRepository).existsByName("VNPay");
        verify(paymentMethodRepository).save(any(PaymentMethod.class));
    }

    @Test
    void createPaymentMethod_NameExists() {
        // Setup
        when(paymentMethodRepository.existsByName("VNPay")).thenReturn(true);

        // Execute & Verify
        assertThrows(ResponseStatusException.class, () -> {
            paymentMethodService.createPaymentMethod(vnpayRequest);
        });
        verify(paymentMethodRepository).existsByName("VNPay");
        verify(paymentMethodRepository, never()).save(any(PaymentMethod.class));
    }

    @Test
    void initializePayment() {
        // Setup
        when(paymentMethodRepository.findById("1")).thenReturn(Optional.of(vnpayMethod));
        
        PaymentRequest request = new PaymentRequest();
        request.setAmount(100000);
        request.setOrderInfo("Test payment");
        
        PaymentResponse expectedResponse = PaymentResponse.builder()
                .success(true)
                .message("Payment URL generated successfully")
                .paymentUrl("https://example.com/pay")
                .build();
        
        when(vnpayProvider.initializePayment(any(PaymentRequest.class))).thenReturn(expectedResponse);

        // Execute
        PaymentResponse result = paymentMethodService.initializePayment("1", request);

        // Verify
        assertTrue(result.isSuccess());
        assertEquals("Payment URL generated successfully", result.getMessage());
        assertEquals("https://example.com/pay", result.getPaymentUrl());
        verify(paymentMethodRepository).findById("1");
        verify(vnpayProvider).initializePayment(any(PaymentRequest.class));
    }

    @Test
    void verifyPayment() {
        // Setup
        when(paymentMethodRepository.findById("1")).thenReturn(Optional.of(vnpayMethod));
        when(vnpayProvider.verifyPayment(anyString())).thenReturn(true);

        // Execute
        boolean result = paymentMethodService.verifyPayment("1", "callback-data");

        // Verify
        assertTrue(result);
        verify(paymentMethodRepository).findById("1");
        verify(vnpayProvider).verifyPayment("callback-data");
    }
}
