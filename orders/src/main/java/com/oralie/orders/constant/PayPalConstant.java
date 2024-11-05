package com.oralie.orders.constant;

import org.springframework.beans.factory.annotation.Value;

public class PayPalConstant {

    @Value("${store.url}")
    private static String urlClient;
    public static final String PAYPAL_SUCCESS_URL = String.format("%s/payment/success", urlClient);
    public static final String PAYPAL_CANCEL_URL = String.format("%s/payment/cancel", urlClient);
    public static final String PAYPAL_CURRENCY = "USD";
    public static final String PAYPAL_INTENT = "sale";
    public static final String SUCCESS_MESSAGE = "Payment successful";
    public static final String CANCEL_MESSAGE = "Payment has been canceled";
    public static final String ERROR_MESSAGE = "Payment has an error";

}
