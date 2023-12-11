package com.stripe.integration.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class CustomerData {

    public String name;
    public String email;

    public String customerId;

    public String source;
    public String amount;
    public String currency;
    public String description;
    public String productId;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

}
