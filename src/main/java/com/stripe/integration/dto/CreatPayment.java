package com.stripe.integration.dto;
import com.sun.istack.NotNull;
import lombok.Data;

import javax.persistence.Entity;

@Data
public class CreatPayment {

    @NotNull
    private Integer amount;


    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

}
