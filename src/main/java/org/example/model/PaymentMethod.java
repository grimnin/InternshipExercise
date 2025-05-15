package org.example.model;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class PaymentMethod {
    private String id;
    private int discount;
    private BigDecimal limit;

    public PaymentMethod() {
    }

    public PaymentMethod(PaymentMethod other) {
        this.id = other.id;
        this.discount = other.discount;
        this.limit = other.limit;
    }
}
