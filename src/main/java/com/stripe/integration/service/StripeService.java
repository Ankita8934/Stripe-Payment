package com.stripe.integration.service;

import ch.qos.logback.classic.Logger;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.integration.dto.StripeChargeDto;
import com.stripe.integration.dto.StripeSubscriptionDto;
import com.stripe.integration.dto.StripeSubscriptionResponse;
import com.stripe.integration.dto.StripeTokenDto;
import com.stripe.integration.entity.CustomerData;
import com.stripe.integration.repository.CustomerRepo;
import com.stripe.model.*;
import com.stripe.param.TokenCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class StripeService {

    @Value("${stripe.key.secret}")
    private String API_SECET_KEY;

    @Autowired
    CustomerRepo customerRepo;

    private Stripe stripe;

    @PostConstruct
    public void init() {
        stripe.apiKey = API_SECET_KEY;
    }

    public CustomerData createCustomer(CustomerData customer) {
        return customerRepo.save(customer);
    }

    public StripeTokenDto createCardToken(StripeTokenDto model) {

        try {
            Map<String, Object> card = new HashMap<>();
            card.put("number", model.getCardNumber());
            card.put("exp_month", Integer.parseInt(model.getExpMonth()));
            card.put("exp_year", Integer.parseInt(model.getExpYear()));
            card.put("cvc", model.getCvc());
            Map<String, Object> params = new HashMap<>();
            params.put("card", card);
            Token token = Token.create(params);
            if (token != null && token.getId() != null) {
                model.setSuccess(true);
                model.setToken(token.getId());
            }
            return model;
        } catch (StripeException e) {
            throw new RuntimeException(e.getMessage());
        }

    }


    public StripeChargeDto charge(StripeChargeDto chargeRequest) {
        try {
            chargeRequest.setSuccess(false);
            Map<String, Object> chargeParams = new HashMap<>();
            chargeParams.put("amount", (int) (chargeRequest.getAmount() * 100));
            chargeParams.put("currency", "USD");
            chargeParams.put("description", "Payment for id " + chargeRequest.getAdditionalInfo().getOrDefault("ID_TAG", ""));
            chargeParams.put("source", chargeRequest.getStripeToken());
            Map<String, Object> metaData = new HashMap<>();
            metaData.put("id", chargeRequest.getChargeId());
            metaData.putAll(chargeRequest.getAdditionalInfo());
            chargeParams.put("metadata", metaData);
            Charge charge = Charge.create(chargeParams);
            chargeRequest.setMessage(charge.getOutcome().getSellerMessage());

            if (charge.getPaid()) {
                chargeRequest.setChargeId(charge.getId());
                chargeRequest.setSuccess(true);

            }
            return chargeRequest;
        } catch (StripeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    public StripeSubscriptionResponse createSubscription(StripeSubscriptionDto subscriptionDto){
        PaymentMethod paymentMethod = createPaymentMethod(subscriptionDto);
        Customer customer = createCustomer(paymentMethod, subscriptionDto);
        paymentMethod = attachCustomerToPaymentMethod(customer, paymentMethod);
        Subscription subscription = createSubscription(subscriptionDto, paymentMethod, customer);
        return createResponse(subscriptionDto,paymentMethod,customer,subscription);
    }


    private StripeSubscriptionResponse createResponse(StripeSubscriptionDto subscriptionDto, PaymentMethod paymentMethod, Customer customer, Subscription subscription) {
        StripeSubscriptionResponse response = new StripeSubscriptionResponse(subscriptionDto, paymentMethod, customer, subscription);
        return response;
    }


    private PaymentMethod createPaymentMethod(StripeSubscriptionDto subscriptionDto){
        try {
            Map<String, Object> card = new HashMap<>();
            card.put("number", subscriptionDto.getCardNumber());
            card.put("exp_month", Integer.parseInt(subscriptionDto.getExpMonth()));
            card.put("exp_year", Integer.parseInt(subscriptionDto.getExpYear()));
            card.put("cvc", subscriptionDto.getCvc());
            Map<String, Object> params = new HashMap<>();
            params.put("type", "card");
            params.put("card", card);
            return PaymentMethod.create(params);
        } catch (StripeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Customer createCustomer(PaymentMethod paymentMethod,StripeSubscriptionDto subscriptionDto){
        try {
            Map<String, Object> customerMap = new HashMap<>();
            customerMap.put("name", subscriptionDto.getUsername());
            customerMap.put("email", subscriptionDto.getEmail());
            customerMap.put("payment_method", paymentMethod.getId());
            return Customer.create(customerMap);
        } catch (StripeException e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    private PaymentMethod attachCustomerToPaymentMethod(Customer customer,PaymentMethod paymentMethod){
        try {
            paymentMethod = com.stripe.model.PaymentMethod.retrieve(paymentMethod.getId());
            Map<String, Object> params = new HashMap<>();
            params.put("customer", customer.getId());
            paymentMethod = paymentMethod.attach(params);
            return paymentMethod;
        } catch (StripeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private Subscription createSubscription(StripeSubscriptionDto subscriptionDto,PaymentMethod paymentMethod,Customer customer){
        try {

            List<Object> items = new ArrayList<>();
            Map<String, Object> item1 = new HashMap<>();
            item1.put(
                    "price",
                    subscriptionDto.getPriceId()
            );
            item1.put("quantity",subscriptionDto.getNumberOfLicense());
            items.add(item1);

            Map<String, Object> params = new HashMap<>();
            params.put("customer", customer.getId());
            params.put("default_payment_method", paymentMethod.getId());
            params.put("items", items);
            return Subscription.create(params);
        } catch (StripeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public  Subscription cancelSubscription(String subscriptionId){

        try {
            Subscription retrieve = Subscription.retrieve(subscriptionId);
            return retrieve.cancel();
        } catch (StripeException e) {

        }
        return null;
    }



}
