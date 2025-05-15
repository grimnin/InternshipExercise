package org.example.data;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.Order;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class OrderLoader {
    public static List<Order> load(String resourceName) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        InputStream inputStream = OrderLoader.class.getClassLoader().getResourceAsStream(resourceName);
        if (inputStream == null) {
            throw new IllegalArgumentException("Plik nie został znaleziony: " + resourceName);
        }
        return Arrays.asList(mapper.readValue(inputStream, Order[].class));
    }
}
