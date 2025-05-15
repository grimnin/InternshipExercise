package org.example;

import org.example.model.Order;
import org.example.model.PaymentMethod;
import org.example.operations.Logic;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class LogicTest {

    @Test
    public void testFullPaymentWithPoints() {
        Order order = new Order();
        order.setId("ORDER1");
        order.setValue(new BigDecimal("100.00"));

        PaymentMethod punkty = new PaymentMethod();
        punkty.setId("PUNKTY");
        punkty.setDiscount(15);
        punkty.setLimit(new BigDecimal("150.00"));

        Map<String, BigDecimal> result = Logic.optimizePayments(List.of(order), List.of(punkty));

        assertNotNull(result.get("PUNKTY"));
        assertEquals(new BigDecimal("85.00"), result.get("PUNKTY").setScale(2));
    }

    @Test
    public void testBestCardPromotionApplied() {
        Order order = new Order();
        order.setId("ORDER2");
        order.setValue(new BigDecimal("200.00"));
        order.setPromotions(List.of("CardA", "CardB"));

        PaymentMethod cardA = new PaymentMethod();
        cardA.setId("CardA");
        cardA.setDiscount(5);
        cardA.setLimit(new BigDecimal("300.00"));

        PaymentMethod cardB = new PaymentMethod();
        cardB.setId("CardB");
        cardB.setDiscount(10);
        cardB.setLimit(new BigDecimal("300.00"));

        Map<String, BigDecimal> result = Logic.optimizePayments(List.of(order), List.of(cardA, cardB));

        assertEquals(new BigDecimal("180.00"), result.get("CardB").setScale(2));
    }

    @Test
    public void testPartialPointsDiscount() {
        Order order = new Order();
        order.setId("ORDER3");
        order.setValue(new BigDecimal("100.00"));

        PaymentMethod punkty = new PaymentMethod();
        punkty.setId("PUNKTY");
        punkty.setDiscount(15); // ignored for partial
        punkty.setLimit(new BigDecimal("20.00"));

        PaymentMethod card = new PaymentMethod();
        card.setId("CardX");
        card.setDiscount(0);
        card.setLimit(new BigDecimal("500.00"));

        Map<String, BigDecimal> result = Logic.optimizePayments(List.of(order), List.of(punkty, card));

        assertEquals(new BigDecimal("20.00"), result.get("PUNKTY").setScale(2));
        assertEquals(new BigDecimal("70.00"), result.get("CardX").setScale(2));
    }

    @Test
    public void testFallbackWithoutPromotion() {
        Order order = new Order();
        order.setId("ORDER4");
        order.setValue(new BigDecimal("50.00"));

        PaymentMethod card = new PaymentMethod();
        card.setId("CardOnly");
        card.setDiscount(0);
        card.setLimit(new BigDecimal("100.00"));

        Map<String, BigDecimal> result = Logic.optimizePayments(List.of(order), List.of(card));

        assertEquals(new BigDecimal("50.00"), result.get("CardOnly").setScale(2));
    }

    @Test
    public void testLimitsRespected() {
        List<Order> orders = new ArrayList<>();

        Order o1 = new Order();
        o1.setId("ORDER5");
        o1.setValue(new BigDecimal("50.00"));
        o1.setPromotions(List.of("CardZ"));

        Order o2 = new Order();
        o2.setId("ORDER6");
        o2.setValue(new BigDecimal("60.00"));
        o2.setPromotions(List.of("CardZ"));

        orders.add(o1);
        orders.add(o2);

        PaymentMethod card = new PaymentMethod();
        card.setId("CardZ");
        card.setDiscount(10);
        card.setLimit(new BigDecimal("100.00")); // starczy tylko na jedno zlecenie

        PaymentMethod backup = new PaymentMethod();
        backup.setId("CardBackup");
        backup.setDiscount(0); // brak rabatu, ale wysoki limit
        backup.setLimit(new BigDecimal("1000.00"));

        Map<String, BigDecimal> result = Logic.optimizePayments(orders, List.of(card, backup));

        // Sprawdź, że obie płatności się odbyły
        BigDecimal totalSpent = result.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        assertEquals(new BigDecimal("105.00"), totalSpent.setScale(2));
        assertNotNull(result.get("CardZ"));
        assertNotNull(result.get("CardBackup"));
    }
}
