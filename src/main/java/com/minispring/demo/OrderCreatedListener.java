package com.minispring.demo;

import com.minispring.annotation.Component;
import com.minispring.annotation.EventListener;

/** Example application component that reacts to OrderCreated events. */
@Component
public class OrderCreatedListener {

    private String lastOrderId;

    @EventListener
    public void onOrderCreated(OrderCreated event) {
        lastOrderId = event.orderId();
        System.out.println("Notification: order created " + event.orderId());
    }

    public String getLastOrderId() {
        return lastOrderId;
    }
}
