package com.stripe.integration.dto;

import lombok.Data;

@Data
public class CheckoutForm {
   private String amount;
   private String email;
   private  String featureRequest;
}
