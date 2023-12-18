package com.stripe.integration.service;

import com.google.gson.JsonSyntaxException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.integration.entity.PriceData;
import com.stripe.integration.repository.PriceRepo;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import com.stripe.net.ApiResource;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentLinkCreateParams;
import com.stripe.param.PriceCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import spark.Request;
import spark.Response;


import java.sql.*;

@Service
public class PaymentService {

    @Value("${stripe.key.secret}")
    private static String stripeKey;

    @Autowired
    PriceRepo priceRepo;

    String endpointSecret = "whsec_c099a0f44f8fd7f48201fe5f6ab5f363bda740d53338ce2eb723ff3d61a9ae95";


    public PaymentLink paymentLink() throws StripeException {
        PaymentLinkCreateParams params =
                PaymentLinkCreateParams.builder()
                        .addLineItem(
                                PaymentLinkCreateParams.LineItem.builder()
                                        .setPrice("price_1OMsShJrNt7jEbo0IDFTrFMw")
                                        .setQuantity(1L)
                                        .build()
                        )
                        .setAfterCompletion(
                                PaymentLinkCreateParams.AfterCompletion.builder()
                                        .setType(PaymentLinkCreateParams.AfterCompletion.Type.REDIRECT)
                                        .setRedirect(
                                                PaymentLinkCreateParams.AfterCompletion.Redirect.builder()
                                                        .setUrl("https://example.com")
                                                        .build()
                                        )
                                        .build()
                        ).addPaymentMethodType(PaymentLinkCreateParams.PaymentMethodType.US_BANK_ACCOUNT)
                        .addPaymentMethodType(PaymentLinkCreateParams.PaymentMethodType.CARD)
                        .build();

        PaymentLink paymentLink = PaymentLink.create(params);
        return paymentLink;
    }

    public Price createPrice(PriceData priceData) throws StripeException {
        try {
            if (priceData.getProductId() == null || priceData.getProductId().isEmpty()) {
                throw new IllegalArgumentException("Product ID cannot be null or empty");
            }
            PriceCreateParams params =
                    PriceCreateParams.builder()
                            .setProduct(priceData.getProductId())
                            .setUnitAmount(priceData.getUnitAmount())
                            .setCurrency(priceData.getCurrency())
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

    public Session paymentStaticAmount(Long amount) throws StripeException {
        long unitAmount = amount * 100;
        SessionCreateParams params =
                SessionCreateParams.builder()
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setPriceData(
                                                SessionCreateParams.LineItem.PriceData.builder()
                                                        .setCurrency("usd")
                                                        .setUnitAmount(unitAmount)
                                                        .setProductData(
                                                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                        .setName("T-shirt")
                                                                        .build())
                                                        .build())
                                        .setQuantity(1L)
                                        .build())
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl("https://example.com/success")
                        .setCancelUrl("https://example.com/cancel")
                        .build();

        Session session = Session.create(params);
        return session;
    }

    public Object handle(Request request, Response response) {
        System.out.println("request==============" +request);
        String payload = request.body();
        System.out.println("payload==============" +payload);
        String sigHeader = request.headers("Stripe-Signature");
        Event event = null;

        try {
            event = Webhook.constructEvent(
                    payload, sigHeader, endpointSecret
            );
        } catch (JsonSyntaxException e) {
            // Invalid payload
            System.out.println("Error parsing payload: " + e.getMessage());
            response.status(400);
        } catch (SignatureVerificationException e) {
            // Invalid signature
            System.out.println("Error verifying webhook signature: " + e.getMessage());
            response.status(400);
        }

        // Deserialize the nested object inside the event
        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject = null;
        if (dataObjectDeserializer.getObject().isPresent()) {
            stripeObject = dataObjectDeserializer.getObject().get();
        } else {
            // Deserialization failed, probably due to an API version mismatch.
            // Refer to the Javadoc documentation on `EventDataObjectDeserializer` for
            // instructions on how to handle this case, or return an error here.
        }

        // Handle the event
        switch (event.getType()) {
            case "payment_intent.succeeded":
                PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
                System.out.println("PaymentIntent was successful!");
                break;
            case "payment_method.attached":
                PaymentMethod paymentMethod = (PaymentMethod) stripeObject;
                System.out.println("PaymentMethod was attached to a Customer!");
                break;
            // ... handle other event types
            default:
                System.out.println("Unhandled event type: " + event.getType());
        }

        response.status(200);
        return "";
    }


}

