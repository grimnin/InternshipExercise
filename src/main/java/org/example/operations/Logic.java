package org.example.operations;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.data.OrderLoader;
import org.example.data.PaymentMethodLoader;
import org.example.model.Order;
import org.example.model.PaymentMethod;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data

public class Logic {
    public static Map<String, BigDecimal> optimizePayments(List<Order> orders, List<PaymentMethod> methods) {
        Map<String, BigDecimal> spentPerMethod = new HashMap<>();
        Map<String, PaymentMethod> methodMap = new HashMap<>();
        for (PaymentMethod m : methods) {
            methodMap.put(m.getId(), new PaymentMethod(m)); // Copy with mutable limit
        }

        for (Order order : orders) {
            BigDecimal orderValue = order.getValue();
            String bestMethod = null;
            BigDecimal maxDiscountValue = BigDecimal.ZERO;
            BigDecimal discountedValue = orderValue;
            PaymentMethod chosenMethod = null;

            // Case 1: Full payment with points (PUNKTY)
            PaymentMethod punkty = methodMap.get("PUNKTY");
            if (punkty != null && punkty.getLimit().compareTo(orderValue) >= 0) {
                BigDecimal punktyDiscount = orderValue.multiply(BigDecimal.valueOf(punkty.getDiscount()))
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                if (punktyDiscount.compareTo(maxDiscountValue) > 0) {
                    maxDiscountValue = punktyDiscount;
                    discountedValue = orderValue.subtract(punktyDiscount);
                    bestMethod = "PUNKTY";
                    chosenMethod = punkty;
                }
            }

            // Case 2: Full payment using best discount from allowed promotions (excluding points)
            if (order.getPromotions() != null) {
                for (String methodId : order.getPromotions()) {
                    PaymentMethod pm = methodMap.get(methodId);
                    if (pm != null && pm.getLimit().compareTo(orderValue) >= 0) {
                        BigDecimal disc = orderValue.multiply(BigDecimal.valueOf(pm.getDiscount()))
                                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                        if (disc.compareTo(maxDiscountValue) > 0) {
                            maxDiscountValue = disc;
                            discountedValue = orderValue.subtract(disc);
                            bestMethod = pm.getId();
                            chosenMethod = pm;
                        }
                    }
                }
            }

            // Case 3: Partial payment with points (>=10% to get 10% discount), rest with any method with funds
            boolean partialPointsApplied = false;
            if (punkty != null && bestMethod == null) {
                BigDecimal tenPercent = orderValue.multiply(BigDecimal.valueOf(0.10)).setScale(2, RoundingMode.HALF_UP);
                if (punkty.getLimit().compareTo(tenPercent) >= 0) {
                    BigDecimal tenPercentDiscount = orderValue.multiply(BigDecimal.valueOf(0.10))
                            .setScale(2, RoundingMode.HALF_UP);
                    BigDecimal totalToPay = orderValue.subtract(tenPercentDiscount);
                    BigDecimal fromPoints = punkty.getLimit().min(totalToPay);
                    BigDecimal remaining = totalToPay.subtract(fromPoints);

                    PaymentMethod backup = findAnyMethodWithFunds(methodMap.values(), remaining);
                    if (backup != null) {
                        punkty.setLimit(punkty.getLimit().subtract(fromPoints));
                        backup.setLimit(backup.getLimit().subtract(remaining));
                        spentPerMethod.merge("PUNKTY", fromPoints, BigDecimal::add);
                        spentPerMethod.merge(backup.getId(), remaining, BigDecimal::add);
                        partialPointsApplied = true;
                    }
                }
            }

            if (!partialPointsApplied) {
                if (chosenMethod != null) {
                    chosenMethod.setLimit(chosenMethod.getLimit().subtract(discountedValue));
                    spentPerMethod.merge(bestMethod, discountedValue, BigDecimal::add);
                } else {
                    // fallback - use first available method even without discount
                    PaymentMethod fallback = findAnyMethodWithFunds(methodMap.values(), orderValue);
                    if (fallback != null) {
                        fallback.setLimit(fallback.getLimit().subtract(orderValue));
                        spentPerMethod.merge(fallback.getId(), orderValue, BigDecimal::add);
                    } else {
                        throw new RuntimeException("Brak dostępnych środków dla zamówienia " + order.getId());
                    }
                }
            }
        }

        return spentPerMethod;
    }

    private static PaymentMethod findAnyMethodWithFunds(Collection<PaymentMethod> methods, BigDecimal needed) {
        for (PaymentMethod method : methods) {
            if (!method.getId().equals("PUNKTY") && method.getLimit().compareTo(needed) >= 0) {
                return method;
            }
        }
        return null;
    }



}
