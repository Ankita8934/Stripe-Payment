package com.stripe.integration.dto;


import lombok.Data;

@Data
public class PriceData {
    String productId;
    Long unitAmount;
    String currency;
}
