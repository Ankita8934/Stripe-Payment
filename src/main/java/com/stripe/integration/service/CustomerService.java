package com.stripe.integration.service;


import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.SetupIntent;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.SetupIntentCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
public class CustomerService {

    @Value("${stripe.key.secret}")
    private static String stripeKey;

    private Stripe stripe;

    @PostConstruct
    public void init() {
        stripe.apiKey = stripeKey;
    }

    public Customer creatCustomer() throws StripeException {
        CustomerCreateParams params =
                CustomerCreateParams.builder()
                        .setEmail("omsingh@gmial.com")
                        .setName("OM Singh")
                        .setShipping(
                                CustomerCreateParams.Shipping.builder()
                                        .setAddress(
                                                CustomerCreateParams.Shipping.Address.builder()
                                                        .setCity("Brothers")
                                                        .setCountry("US")
                                                        .setLine1("27 Fredrick Ave")
                                                        .setPostalCode("97712")
                                                        .setState("CA")
                                                        .build()
                                        )
                                        .setName("Om")
                                        .build()
                        )
                        .setAddress(
                                CustomerCreateParams.Address.builder()
                                        .setCity("Brothers")
                                        .setCountry("US")
                                        .setLine1("27 Fredrick Ave")
                                        .setPostalCode("97712")
                                        .setState("CA")
                                        .build()
                        )
                        .build();

        Customer customer = Customer.create(params);
        return customer;
    }

    public Map<String, String> createSetupIntent() throws StripeException {
        CustomerCreateParams params1 =
                CustomerCreateParams.builder()
                        .setName("Om Dutt")
                        .setEmail("omduttsingh@gmail.com")
                        .build();
        Customer customer = Customer.create(params1);
        SetupIntentCreateParams params = SetupIntentCreateParams
                .builder()
                .setCustomer(customer.getId())
                .setAutomaticPaymentMethods(
                        SetupIntentCreateParams.AutomaticPaymentMethods.builder().setEnabled(true).build()
                )
                .build();
        SetupIntent setupIntent = SetupIntent.create(params);
        String clientSecret = setupIntent.getClientSecret();
        Map<String, String> result = new HashMap<>();
        result.put("client_secret", clientSecret);
        return result;
    }
}
