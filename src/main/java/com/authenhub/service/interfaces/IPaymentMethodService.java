package com.authenhub.service.interfaces;

import com.authenhub.bean.payment.PaymentRequest;
import com.authenhub.bean.payment.PaymentResponse;
import com.authenhub.bean.payment.PaymentMethodRequest;
import com.authenhub.bean.payment.PaymentMethodResponse;

import java.util.List;

/**
 * Interface for payment method service operations
 */
public interface IPaymentMethodService {

    /**
     * Get all payment methods
     *
     * @return list of payment methods
     */
    List<PaymentMethodResponse> getAllPaymentMethods();

    /**
     * Get active payment methods
     *
     * @return list of active payment methods
     */
    List<PaymentMethodResponse> getActivePaymentMethods();

    /**
     * Get payment method by id
     *
     * @param id payment method id
     * @return payment method
     */
    PaymentMethodResponse getPaymentMethodById(String id);

    /**
     * Create a new payment method
     *
     * @param request payment method request
     * @return created payment method
     */
    PaymentMethodResponse createPaymentMethod(PaymentMethodRequest request);

    /**
     * Update a payment method
     *
     * @param id payment method id
     * @param request payment method request
     * @return updated payment method
     */
    PaymentMethodResponse updatePaymentMethod(String id, PaymentMethodRequest request);

    /**
     * Delete a payment method
     *
     * @param id payment method id
     */
    void deletePaymentMethod(String id);

    /**
     * Initialize a payment
     *
     * @param paymentMethodId payment method id
     * @param request payment request
     * @return payment response
     */
    PaymentResponse initializePayment(String paymentMethodId, PaymentRequest request);

    /**
     * Verify a payment callback
     *
     * @param paymentMethodId payment method id
     * @param callbackData callback data from payment provider
     * @return true if payment is valid, false otherwise
     */
    boolean verifyPayment(String paymentMethodId, String callbackData);
}
