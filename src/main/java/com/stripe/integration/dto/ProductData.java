package com.stripe.integration.dto;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class ProductData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    public String productId;
    private String productName;
}
