package com.stripe.integration.controller;


import com.stripe.exception.StripeException;
import com.stripe.integration.entity.CreatPayment;
import com.stripe.integration.entity.PaymentResponse;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class PaymentController {

    @PostMapping("/create-payment-intent")
    public PaymentResponse createPaymentIntent(@RequestBody @Valid CreatPayment creatPayment) throws StripeException, StripeException {
        PaymentIntentCreateParams createParams = new
                PaymentIntentCreateParams.Builder()
                .setCurrency("usd")
                .putMetadata("featureRequest", creatPayment.getFeatureRequest())
                .setAmount(creatPayment.getAmount() * 100L)
                .build();

        PaymentIntent intent = PaymentIntent.create(createParams);
        return new PaymentResponse(intent.getClientSecret());
    }
}
