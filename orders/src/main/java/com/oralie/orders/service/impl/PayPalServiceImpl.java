package com.oralie.orders.service.impl;

import com.oralie.orders.dto.request.PayPalInfoRequest;
import com.oralie.orders.service.OrderService;
import com.oralie.orders.service.PayPalService;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.OAuthTokenCredential;
import com.paypal.base.rest.PayPalRESTException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class PayPalServiceImpl implements PayPalService {

    @Value("${paypal.client-id}")
    private String clientId;

    @Value("${paypal.client-secret}")
    private String clientSecret;

    @Value("${paypal.mode}")
    private String mode;

    private final OrderService orderService;

    public Payment placePaypalPayment(PayPalInfoRequest payPalInfoRequest) throws PayPalRESTException {

        APIContext apiContext = new APIContext(getAccessToken());
        apiContext.setConfigurationMap(getDefaultConfigurationMap());


        Payer payer = new Payer();
        payer.setPaymentMethod("paypal");


        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(payPalInfoRequest.getCancelUrl());
        redirectUrls.setReturnUrl(payPalInfoRequest.getSuccessUrl());

        Details details = new Details();
        details.setShipping("0");
        details.setSubtotal(String.valueOf(payPalInfoRequest.getTotal()));
        details.setTax("0");


        Amount amount = new Amount();
        amount.setCurrency(payPalInfoRequest.getCurrency());
        amount.setTotal(String.valueOf(payPalInfoRequest.getTotal()));
        amount.setDetails(details);


        Transaction transaction = new Transaction();
        transaction.setDescription(payPalInfoRequest.getDescription());
        transaction.setAmount(amount);


        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        Payment payment = new Payment();
        payment.setIntent(payPalInfoRequest.getIntent());
        payment.setPayer(payer);
        payment.setRedirectUrls(redirectUrls);
        payment.setTransactions(transactions);


        return payment.create(apiContext);

    }


    public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException {
        try {
            Payment payment = new Payment();
            payment.setId(paymentId);
            PaymentExecution paymentExecution = new PaymentExecution();
            paymentExecution.setPayerId(payerId);
            APIContext apiContext = new APIContext(getAccessToken());
            apiContext.setConfigurationMap(getDefaultConfigurationMap());

            Payment paymentExecuted = payment.execute(apiContext, paymentExecution);

            orderService.updateOrderPaymentStatusByPayPalId(paymentExecuted.getId(), paymentExecuted.getState());

            return paymentExecuted;
        } catch (PayPalRESTException e) {
            throw new PayPalRESTException("Error executing payment", e);
        }
    }

    private String getAccessToken() throws PayPalRESTException {
        return new OAuthTokenCredential(clientId, clientSecret, getDefaultConfigurationMap()).getAccessToken();
    }


    private java.util.Map<String, String> getDefaultConfigurationMap() {
        java.util.Map<String, String> configurationMap = new java.util.HashMap<>();
        configurationMap.put("mode", mode);
        return configurationMap;
    }

}
