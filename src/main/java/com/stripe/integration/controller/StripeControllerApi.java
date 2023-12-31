package com.stripe.integration.controller;


import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.integration.dto.*;
import com.stripe.integration.entity.CustomerData;
import com.stripe.integration.entity.PriceData;
import com.stripe.integration.entity.ProductData;
import com.stripe.integration.entity.SubscriptionData;
import com.stripe.integration.repository.CustomerRepo;
import com.stripe.integration.service.StripeService;
import com.stripe.model.*;
import com.stripe.param.CustomerCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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


    @RequestMapping("/create_product")
    public Product createProduct(@RequestBody ProductData productData) throws StripeException
    {
        Stripe.apiKey = stripeKey;
        return stripeService.createProduct(productData);
    }

    @RequestMapping("/create_price")
    public Price createPrice(@RequestBody PriceData priceData) throws StripeException
    {
        Stripe.apiKey = stripeKey;

        return stripeService.createPrice(priceData);
    }

    @RequestMapping("/create_price_yearly")
    public Price createPriceYearly(@RequestBody PriceData priceData) throws StripeException
    {
        Stripe.apiKey = stripeKey;

        return stripeService.createPriceYearly(priceData);
    }

    @RequestMapping("/create-subscription")
    public String subscriptionCreation() throws StripeException{
        Stripe.apiKey = stripeKey;
        return stripeService.subscribe();
    }

    @RequestMapping("/default")
    public List<PaymentMethod> getPaymentDefaultMethodsForCustomer(@RequestParam String customerId) throws StripeException {
        Stripe.apiKey = stripeKey;
        return stripeService.getPaymentMethodsForCustomer(customerId);
    }

    @RequestMapping("/attachPaymentMethodToCustomer")
    public void attachPaymentMethodToCustomer(@RequestParam String customerId,@RequestParam String paymentMethodId) throws StripeException {
        Stripe.apiKey = stripeKey;
        stripeService.attachPaymentMethodToCustomer(customerId,paymentMethodId);
    }

    @RequestMapping("/setDefaultPaymentMethod")
    public void setDefaultPaymentMethod(@RequestParam String customerId,@RequestParam String paymentMethodId) {
        Stripe.apiKey = stripeKey;
        stripeService.setDefaultPaymentMethod(customerId,paymentMethodId);
    }


    @RequestMapping("/repayment")
    public void PaymentMethod() throws StripeException {
           Stripe.apiKey = stripeKey;
            stripeService.createPaymentMethod();
    }
    public  void createOffSessionPayment() throws  StripeException{
        Stripe.apiKey = stripeKey;
        stripeService.createOffsessionPyment();
    }


}

