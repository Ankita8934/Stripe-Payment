package com.stripe.integration.entity;
import com.sun.istack.NotNull;
import lombok.Data;

import javax.persistence.Entity;

@Data
public class CreatPayment {

    @NotNull
    private Integer amount;

    @NotNull
    private String featureRequest;

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getFeatureRequest() {
        return featureRequest;
    }

    public void setFeatureRequest(String featureRequest) {
        this.featureRequest = featureRequest;
    }
}
