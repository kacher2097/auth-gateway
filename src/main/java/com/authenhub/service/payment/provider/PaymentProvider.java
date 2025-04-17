package com.authenhub.service.payment.provider;

import com.authenhub.bean.payment.PaymentRequest;
import com.authenhub.bean.payment.PaymentResponse;

/**
 * Interface for payment providers
 */
public interface PaymentProvider {
    
    /**
     * Initialize a payment
     *
     * @param request payment request
     * @return payment response with payment URL or data
     */
    PaymentResponse initializePayment(PaymentRequest request);
    
    /**
     * Verify a payment callback
     *
     * @param callbackData callback data from payment provider
     * @return true if payment is valid, false otherwise
     */
    boolean verifyPayment(String callbackData);
    
    /**
     * Get payment provider name
     *
     * @return provider name
     */
    String getProviderName();
    
    /**
     * Check if provider is available
     *
     * @return true if provider is available, false otherwise
     */
    boolean isAvailable();
}
