package org.example;

import org.example.data.OrderLoader;
import org.example.data.PaymentMethodLoader;
import org.example.model.Order;
import org.example.model.PaymentMethod;
import org.example.operations.Logic;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class App
{
    public static void main( String[] args )
    {
        if (args.length != 2) {
            System.err.println("Użycie: java -jar app.jar orders.json paymentmethods.json");
            System.exit(1);
        }

        String ordersPath = args[0];
        String methodsPath = args[1];

        try {
            List<Order> orders = OrderLoader.load(ordersPath);
            List<PaymentMethod> methods = PaymentMethodLoader.load(methodsPath);

            Map<String, BigDecimal> result = Logic.optimizePayments(orders, methods);

            result.forEach((method, value) -> System.out.println(method + " " + value.setScale(2)));

        } catch (Exception e) {
            System.err.println("Błąd: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
