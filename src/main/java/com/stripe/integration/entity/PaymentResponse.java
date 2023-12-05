package com.stripe.integration.entity;

import lombok.Data;

@Data
public class PaymentResponse {
    private String clientSecrete;

    public PaymentResponse(String clientSecrete){
        this.clientSecrete=clientSecrete;
    }
}
