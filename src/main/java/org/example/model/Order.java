package org.example.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
@Data
public class Order {
    private String id;
    private BigDecimal value;
    private List<String> promotions;

    public Order() {

    }

    public Order(Order other) {
        this.id = other.id;
        this.value = other.value;
        // Tworzymy nową listę, żeby nie dzielić referencji
        this.promotions = (other.promotions != null) ? new ArrayList<>(other.promotions) : null;
    }
}
