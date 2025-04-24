package com.authenhub.service.payment;

import com.authenhub.bean.payment.PaymentMethodRequest;
import com.authenhub.bean.payment.PaymentMethodResponse;
import com.authenhub.bean.payment.PaymentRequest;
import com.authenhub.bean.payment.PaymentResponse;
import com.authenhub.entity.mongo.PaymentMethod;
import com.authenhub.repository.PaymentMethodRepository;
import com.authenhub.service.interfaces.IPaymentMethodService;
import com.authenhub.service.payment.provider.PaymentProvider;
import com.authenhub.utils.TimestampUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentMethodService implements IPaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;
    private final Map<String, PaymentProvider> paymentProviders;

    @Override
    public List<PaymentMethodResponse> getAllPaymentMethods() {
        return paymentMethodRepository.findAll().stream()
                .map(PaymentMethodResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<PaymentMethodResponse> getActivePaymentMethods() {
        return paymentMethodRepository.findByIsActiveTrue().stream()
                .map(PaymentMethodResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public PaymentMethodResponse getPaymentMethodById(String id) {
        return paymentMethodRepository.findById(id)
                .map(PaymentMethodResponse::fromEntity)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Payment method not found"));
    }

    @Override
    public PaymentMethodResponse createPaymentMethod(PaymentMethodRequest request) {
        // Check if payment method with the same name already exists
        if (paymentMethodRepository.existsByName(request.getName())) {
            throw new ResponseStatusException(BAD_REQUEST, "Payment method with this name already exists");
        }

        // Check if provider type is valid
        if (!paymentProviders.containsKey(request.getProviderType())) {
            throw new ResponseStatusException(BAD_REQUEST, "Invalid provider type");
        }

        // Create payment method
        PaymentMethod paymentMethod = PaymentMethod.builder()
                .name(request.getName())
                .displayName(request.getDisplayName())
                .description(request.getDescription())
                .providerType(request.getProviderType())
                .isActive(request.isActive())
                .config(request.getConfig())
                .iconUrl(request.getIconUrl())
                .feePercentage(request.getFeePercentage())
                .fixedFee(request.getFixedFee())
                .currency(request.getCurrency())
                .createdAt(TimestampUtils.now())
                .updatedAt(TimestampUtils.now())
                .build();
        paymentMethod = paymentMethodRepository.save(paymentMethod);

        return PaymentMethodResponse.fromEntity(paymentMethod);
    }

    @Override
    public PaymentMethodResponse updatePaymentMethod(String id, PaymentMethodRequest request) {
        // Find payment method
        PaymentMethod paymentMethod = paymentMethodRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Payment method not found"));

        // Check if name is being changed and if it already exists
        if (!paymentMethod.getName().equals(request.getName()) &&
                paymentMethodRepository.existsByName(request.getName())) {
            throw new ResponseStatusException(BAD_REQUEST, "Payment method with this name already exists");
        }

        // Check if provider type is valid
        if (!paymentProviders.containsKey(request.getProviderType())) {
            throw new ResponseStatusException(BAD_REQUEST, "Invalid provider type");
        }

        // Update payment method
        paymentMethod.setName(request.getName());
        paymentMethod.setDisplayName(request.getDisplayName());
        paymentMethod.setDescription(request.getDescription());
        paymentMethod.setProviderType(request.getProviderType());
        paymentMethod.setActive(request.isActive());
        paymentMethod.setConfig(request.getConfig());
        paymentMethod.setIconUrl(request.getIconUrl());
        paymentMethod.setFeePercentage(request.getFeePercentage());
        paymentMethod.setFixedFee(request.getFixedFee());
        paymentMethod.setCurrency(request.getCurrency());
        paymentMethod.setUpdatedAt(TimestampUtils.now());

        paymentMethod = paymentMethodRepository.save(paymentMethod);

        return PaymentMethodResponse.fromEntity(paymentMethod);
    }

    @Override
    public void deletePaymentMethod(String id) {
        // Check if payment method exists
        if (!paymentMethodRepository.existsById(id)) {
            throw new ResponseStatusException(NOT_FOUND, "Payment method not found");
        }

        // Delete payment method
        paymentMethodRepository.deleteById(id);
    }

    @Override
    public PaymentResponse initializePayment(String paymentMethodId, PaymentRequest request) {
        // Find payment method
        PaymentMethod paymentMethod = paymentMethodRepository.findById(paymentMethodId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Payment method not found"));

        // Check if payment method is active
        if (!paymentMethod.isActive()) {
            return PaymentResponse.builder()
                    .success(false)
                    .message("Payment method is not active")
                    .build();
        }

        // Get payment provider
        PaymentProvider provider = getPaymentProvider(paymentMethod.getProviderType());

        // Check if provider is available
        if (!provider.isAvailable()) {
            return PaymentResponse.builder()
                    .success(false)
                    .message("Payment provider is not available")
                    .build();
        }

        // Initialize payment
        return provider.initializePayment(request);
    }

    @Override
    public boolean verifyPayment(String paymentMethodId, String callbackData) {
        // Find payment method
        PaymentMethod paymentMethod = paymentMethodRepository.findById(paymentMethodId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Payment method not found"));

        // Get payment provider
        PaymentProvider provider = getPaymentProvider(paymentMethod.getProviderType());

        // Verify payment
        return provider.verifyPayment(callbackData);
    }

    private PaymentProvider getPaymentProvider(String providerType) {
        return Optional.ofNullable(paymentProviders.get(providerType))
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "Invalid provider type"));
    }
}
