package com.minispring.context;

import com.minispring.demo.OrderCreatedListener;
import com.minispring.demo.OrderService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApplicationContextEventIntegrationTest {

    @Test
    void publishesEventFromServiceToDiscoveredListener() throws Exception {
        Environment environment = new Environment();
        environment.addProfile("dev");
        environment.setProperty("feature.enabled", "true");

        try (ApplicationContext context =
                     new ApplicationContext("com.minispring.demo", environment)) {
            OrderService orders = context.getBean(OrderService.class);
            OrderCreatedListener listener = context.getBean(OrderCreatedListener.class);

            orders.placeOrder("integration-42");

            assertEquals("integration-42", listener.getLastOrderId());
        }
    }
}
