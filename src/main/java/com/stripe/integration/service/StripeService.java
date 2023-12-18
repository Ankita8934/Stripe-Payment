package com.stripe.integration.service;

import com.stripe.Stripe;
import com.stripe.exception.CardException;
import com.stripe.exception.StripeException;
import com.stripe.integration.dto.*;
import com.stripe.integration.entity.CustomerData;
import com.stripe.integration.entity.PriceData;
import com.stripe.integration.entity.ProductData;
import com.stripe.integration.entity.SubscriptionData;
import com.stripe.integration.repository.CustomerRepo;
import com.stripe.integration.repository.PriceRepo;
import com.stripe.integration.repository.ProductRepo;
import com.stripe.integration.repository.SubscriptionDataRepo;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.stripe.param.*;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;


@Service
public class StripeService {


    @Value("${stripe.key.secret}")
    private static String stripeKey;

    @Autowired
    CustomerRepo customerRepo;

    @Autowired
    ProductRepo productRepo;

    @Autowired
    PriceRepo priceRepo;

    @Autowired
    SubscriptionDataRepo subscriptionDataRepo;

    private Stripe stripe;

    @PostConstruct
    public void init() {
        stripe.apiKey = stripeKey;
    }

    public CustomerData createCustomer(CustomerData customerData) throws StripeException {
        CustomerCreateParams params =
                CustomerCreateParams.builder()
                        .setName(customerData.getName())
                        .setEmail(customerData.getEmail())
                        .build();
        Customer customer = Customer.create(params);
        customerData.setCustomerId(customer.getId());
        return customerRepo.save(customerData);
    }


    public StripeTokenDto createCardToken(StripeTokenDto model) {
        Stripe.apiKey = stripeKey;
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
            chargeParams.put("currency", "eur");
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

    public Product createProduct(ProductData productData) throws StripeException {

        ProductCreateParams params =
                ProductCreateParams.builder()
                        .setName(productData.getProductName())
                        .build();
        Product product = Product.create(params);
        productData.setProductId(product.getId());
        productRepo.save(productData);
        return product;
    }


    public Price createPrice(PriceData priceData) throws StripeException {
        PriceCreateParams params =
                PriceCreateParams.builder()
                        .setProduct(priceData.getProductId())
                        .setUnitAmount(priceData.getUnitAmount())
                        .setCurrency(priceData.getCurrency())
                        .setRecurring(
                                PriceCreateParams.Recurring.builder()
                                        .setInterval(PriceCreateParams.Recurring.Interval.MONTH)
                                        .build()
                        )
                        .build();
        Price price = Price.create(params);
        priceData.setProductId(price.getId());
        priceRepo.save(priceData);
        return price;
    }

    public Price createPriceYearly(PriceData priceData) throws StripeException {
        try {
            if (priceData.getProductId() == null || priceData.getProductId().isEmpty()) {
                throw new IllegalArgumentException("Product ID cannot be null or empty");
            }
            PriceCreateParams params =
                    PriceCreateParams.builder()
                            .setProduct(priceData.getProductId())
                            .setUnitAmount(priceData.getUnitAmount())
                            .setCurrency(priceData.getCurrency())
                            .setRecurring(
                                    PriceCreateParams.Recurring.builder()
                                            .setInterval(PriceCreateParams.Recurring.Interval.YEAR)
                                            .build()
                            )
                            .build();

            Price price = Price.create(params);
            priceData.setProductId(price.getId());
            priceRepo.save(priceData);
            return price;
        } catch (StripeException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public List<PaymentMethod> getPaymentMethodsForCustomer(String customerId) throws StripeException {
        PaymentMethodListParams params = PaymentMethodListParams.builder()
                .setCustomer(customerId)
                .setType(PaymentMethodListParams.Type.CARD)
                .build();
        PaymentMethodCollection paymentMethodCollection = PaymentMethod.list(params);
        System.out.println("payment method data------"  +paymentMethodCollection);
        return paymentMethodCollection.getData();
    }

    public void attachPaymentMethodToCustomer(String customerId, String paymentMethodId) {
        try {
            PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentMethodId);
            PaymentMethodAttachParams params = PaymentMethodAttachParams.builder()
                    .setCustomer(customerId)
                    .build();
            paymentMethod.attach(params);
            System.out.println("Payment method attached successfully to customer: " + customerId);
        } catch (StripeException e) {
            e.printStackTrace();
        }
    }

    public void setDefaultPaymentMethod(String customerId, String paymentMethodId) {
        try {
            Customer customer = Customer.retrieve(customerId);
            CustomerUpdateParams params = CustomerUpdateParams.builder()
                    .setInvoiceSettings(CustomerUpdateParams.InvoiceSettings.builder()
                            .setDefaultPaymentMethod(paymentMethodId)
                            .build())
                    .build();
            customer.update(params);
            System.out.println("Default payment method set successfully for customer: " + customerId);
        } catch (StripeException e) {
            e.printStackTrace();
        }
    }

    public void createPaymentMethod() throws StripeException {
        PaymentMethodListParams params =
                PaymentMethodListParams.builder()
                        .setCustomer("{{CUSTOMER_ID}}")
                        .setType(PaymentMethodListParams.Type.CARD)
                        .build();
        PaymentMethodCollection paymentMethods = PaymentMethod.list(params);
    }

    public  void createOffsessionPyment() throws  StripeException{
        PaymentIntentCreateParams params =
                PaymentIntentCreateParams.builder()
                        .setCurrency("eur")
                        .setAmount(1099L)
                        // In the latest version of the API, specifying the `automatic_payment_methods` parameter is optional because Stripe enables its functionality by default.
                        .setAutomaticPaymentMethods(
                                PaymentIntentCreateParams.AutomaticPaymentMethods.builder().setEnabled(true).build()
                        )
                        .setCustomer("{{CUSTOMER_ID}}")
                        .setPaymentMethod("{{PAYMENT_METHOD_ID}}")
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
    }
    public String subscribe() throws StripeException {

        SubscriptionCreateParams.PaymentSettings paymentSettings = SubscriptionCreateParams.PaymentSettings
                .builder()
               .build();

        // Build subscription creation parameters
        SubscriptionCreateParams subCreateParams = SubscriptionCreateParams
                .builder()
                .setCustomer("cus_PASCDmKvzI4MZ0")
                .addItem(
                        SubscriptionCreateParams.Item.builder()
                                .setPrice("price_1OKwmvSHoSEFFcLa8ykQcxHQ")
                                .build()
                )
                .setPaymentSettings(paymentSettings)
                .setPaymentBehavior(SubscriptionCreateParams.PaymentBehavior.DEFAULT_INCOMPLETE)
                .addAllExpand(Arrays.asList("latest_invoice.payment_intent", "pending_setup_intent"))
                .build();

        // Create the subscription
        Subscription subscription = Subscription.create(subCreateParams);

        // Prepare the response data
        Map<String, Object> responseData = new HashMap<>();

        if (subscription.getPendingSetupIntentObject() != null) {
            // If there is a pending setup intent, treat it as a setup
            responseData.put("type", "setup");
            responseData.put("clientSecret", subscription.getPendingSetupIntentObject().getClientSecret());
        } else {
            // If there is no pending setup intent, treat it as a payment
            responseData.put("type", "payment");
            responseData.put("clientSecret", subscription.getLatestInvoiceObject().getPaymentIntentObject().getClientSecret());
        }

        // Convert the response data to JSON and return it
        return StripeObject.PRETTY_PRINT_GSON.toJson(responseData);
    }

public void createSession() throws StripeException{
    SessionCreateParams params = new SessionCreateParams.Builder()
            .setSuccessUrl("https://example.com/success.html?session_id={CHECKOUT_SESSION_ID}")
            .setCancelUrl("https://example.com/canceled.html")
            .setMode(SessionCreateParams.Mode.SUBSCRIPTION)
            .addLineItem(new SessionCreateParams.LineItem.Builder()
                    // For metered billing, do not pass quantity
                    .setQuantity(1L)
                    .setPrice("price_1OM7jeSHoSEFFcLaZcZChJqS")
                    .build()
            )
            .build();

    Session session = Session.create(params);
}


}
