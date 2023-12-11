package com.stripe.integration.controller;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.integration.entity.CustomerData;
import com.stripe.integration.service.CustomerService;
import com.stripe.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class CustomerController {

    @Value("${stripe.key.secret}")
    String stripeKey;

    @Value("${stripe.key.public}")
    String stripePublicKey;

    @Autowired
    CustomerService customerService;


    @RequestMapping("/create-customer")
    public Customer creatCustomer() throws StripeException {
        Stripe.apiKey = stripeKey;
       return customerService.creatCustomer();

    }

    @RequestMapping("/create-intent")
    public  Map<String, String> createSetupIntent() throws StripeException {
        Stripe.apiKey = stripeKey;
        return customerService.createSetupIntent();
    }
}
