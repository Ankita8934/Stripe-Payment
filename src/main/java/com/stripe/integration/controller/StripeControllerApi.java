package com.stripe.integration.controller;


import ch.qos.logback.classic.util.LogbackMDCAdapter;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.integration.dto.*;
import com.stripe.integration.entity.CreatPayment;
import com.stripe.integration.entity.CustomerData;
import com.stripe.integration.entity.PaymentResponse;
import com.stripe.integration.repository.CustomerRepo;
import com.stripe.integration.service.StripeService;
import com.stripe.model.*;
import com.stripe.net.RequestOptions;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.ProductCreateParams;
import com.stripe.param.SubscriptionCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.nonNull;

@RestController
public class StripeControllerApi {
    @Value("${stripe.key.secret}")
    String stripeKey;

    @Value("${stripe.key.public}")
    String stripePublicKey;

    @Autowired
    StripeService stripeService;

    @Autowired
    CustomerRepo customerRepo;

    @RequestMapping("/createCustomers")
    public CustomerData createCustomer(@RequestBody CustomerData data) throws StripeException {
        Stripe.apiKey = stripeKey;
        CustomerCreateParams params =
                CustomerCreateParams.builder()
                        .setName(data.getName())
                        .setEmail(data.getEmail())
                        .build();
        Customer customer = Customer.create(params);
        data.setCustomerId(customer.getId());
        return stripeService.createCustomer(data);
    }

    @RequestMapping("/getAllCustomers")
    public List<CustomerData> getAllCustomer() throws StripeException {
        Stripe.apiKey = stripeKey;

        Map<String, Object> params = new HashMap<>();
        params.put("limit", 3);

        CustomerCollection customers = Customer.list(params);
        List<CustomerData> allCustomer = new ArrayList<CustomerData>();
        for (int i = 0; i < customers.getData().size(); i++) {
            CustomerData customerData = new CustomerData();
            customerData.setCustomerId(customers.getData().get(i).getId());
            customerData.setName(customers.getData().get(i).getName());
            customerData.setEmail(customers.getData().get(i).getEmail());
            allCustomer.add(customerData);
        }
        return allCustomer;
    }

    @PostMapping("/card/token")
    @ResponseBody
    public StripeTokenDto createCardToken(@RequestBody StripeTokenDto model) {
        return stripeService.createCardToken(model);
    }

    @PostMapping("/charge")
    @ResponseBody
    public StripeChargeDto charge(@RequestBody StripeChargeDto model) {

        return stripeService.charge(model);
    }

    @PostMapping("/customer/subscription")
    @ResponseBody
    public StripeSubscriptionResponse subscription(@RequestBody StripeSubscriptionDto model) {

        return stripeService.createSubscription(model);
    }

    @DeleteMapping("/subscription/{id}")
    @ResponseBody
    public SubscriptionCancelRecord cancelSubscription(@PathVariable String id){

        Subscription subscription = stripeService.cancelSubscription(id);
        if(nonNull(subscription)){

            return new SubscriptionCancelRecord(subscription.getStatus());
        }

        return null;
    }


}
