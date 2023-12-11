package com.stripe.integration.controller;


import com.stripe.Stripe;
import com.stripe.exception.CardException;
import com.stripe.exception.StripeException;
import com.stripe.integration.dto.CreatPayment;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class PaymentController {

    @Value("${stripe.key.secret}")
    String stripeKey;

    @PostMapping("/create-payment-intent")
    public  CreatPayment createPaymentIntent(@RequestBody @Valid CreatPayment creatPayment) throws StripeException, StripeException {
        Stripe.apiKey = stripeKey;
        PaymentIntentCreateParams params =
                PaymentIntentCreateParams.builder()
                        .setCurrency("inr")
                        .setAmount(9700l)
                        // In the latest version of the API, specifying the `automatic_payment_methods` parameter is optional because Stripe enables its functionality by default.
                        .setAutomaticPaymentMethods(
                                PaymentIntentCreateParams.AutomaticPaymentMethods.builder().setEnabled(true).build()
                        )
                        .setCustomer("cus_PASCDmKvzI4MZ0")
                        .setPaymentMethod("pm_1OM7INSHoSEFFcLapycPqS0x")
                        .setReturnUrl("https://example.com/order/123/complete")
                        .setConfirm(true)
                        .setOffSession(true)
                        .build();
        try {
            PaymentIntent.create(params);
        } catch (CardException e) {
            // Error code will be authentication_required if authentication is needed
            System.out.println("Error code is : " + e.getCode());
            String paymentIntentId = e.getStripeError().getPaymentIntent().getId();
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
            System.out.println(paymentIntent.getId());
        }
        return creatPayment;
    }
}
