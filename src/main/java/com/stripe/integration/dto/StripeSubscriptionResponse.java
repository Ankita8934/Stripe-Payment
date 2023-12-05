package com.stripe.integration.dto;

import com.stripe.model.Customer;
import com.stripe.model.PaymentMethod;
import com.stripe.model.Subscription;
import lombok.Data;
import org.apache.tomcat.jni.User;

@Data
public class StripeSubscriptionResponse {
    public StripeSubscriptionResponse(String stripeCustomerId, String stripeSubscriptionId, String stripePaymentMethodId, String username) {
        this.stripeCustomerId = stripeCustomerId;
        this.stripeSubscriptionId = stripeSubscriptionId;
        this.stripePaymentMethodId = stripePaymentMethodId;
        this.username = username;
    }

    private String stripeCustomerId;
    private String stripeSubscriptionId;
    private String stripePaymentMethodId;
    private String username;

    public StripeSubscriptionResponse(StripeSubscriptionDto subscriptionDto, PaymentMethod paymentMethod, Customer customer, Subscription subscription) {

    }
}
