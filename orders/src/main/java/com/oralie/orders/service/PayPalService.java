package com.oralie.orders.service;

import com.oralie.orders.dto.request.PayPalInfoRequest;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;

public interface PayPalService {
    Payment placePaypalPayment(PayPalInfoRequest payPalInfoRequest) throws PayPalRESTException;

    Payment executePayment(String paymentId, String payerId) throws PayPalRESTException;
}
