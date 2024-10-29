package com.oralie.orders.service.impl;

import com.oralie.orders.dto.request.PayPalInfoRequest;
import com.oralie.orders.service.PayPalService;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class PayPalServiceImpl implements PayPalService {

    private final APIContext apiContext;

    @Override
    public Payment placePaypalPayment(PayPalInfoRequest payPalInfoRequest) throws PayPalRESTException {
        Amount amount = new Amount();
        amount.setCurrency(payPalInfoRequest.getCurrency());
        amount.setTotal(String.format(Locale.forLanguageTag(payPalInfoRequest.getCurrency()), "%.2f", payPalInfoRequest.getTotal())); // 9.99$ - 9,99€

        Transaction transaction = new Transaction();
        transaction.setDescription(payPalInfoRequest.getDescription());
        transaction.setAmount(amount);

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod(payPalInfoRequest.getMethod());

        Payment payment = new Payment();
        payment.setIntent(payPalInfoRequest.getIntent());
        payment.setPayer(payer);
        payment.setTransactions(transactions);

        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(payPalInfoRequest.getCancelUrl());
        redirectUrls.setReturnUrl(payPalInfoRequest.getSuccessUrl());

        payment.setRedirectUrls(redirectUrls);

        return payment.create(apiContext);
    }

    public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException {
        Payment payment = new Payment();
        payment.setId(paymentId);

        PaymentExecution paymentExecution = new PaymentExecution();
        paymentExecution.setPayerId(payerId);

        return payment.execute(apiContext, paymentExecution);
    }
}
