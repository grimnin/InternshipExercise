package org.example.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.PaymentMethod;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;


public class PaymentMethodLoader {

    public static List<PaymentMethod> load(String resourceName) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        InputStream inputStream = PaymentMethodLoader.class.getClassLoader().getResourceAsStream(resourceName);

        if (inputStream == null) {
            throw new IllegalArgumentException("Plik nie został znaleziony: " + resourceName);
        }

        return Arrays.asList(mapper.readValue(inputStream, PaymentMethod[].class));
    }
}
