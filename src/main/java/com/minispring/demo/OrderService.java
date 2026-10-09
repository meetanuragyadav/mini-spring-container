package com.minispring.demo;

import com.minispring.annotation.Component;
import com.minispring.annotation.Inject;
import com.minispring.annotation.Qualifier;
import com.minispring.events.EventPublisher;

import java.util.Objects;

/** Demonstrates publishing an event after the core order action succeeds. */
@Component
public class OrderService {

    private final PaymentGateway gateway;
    private final EventPublisher eventPublisher;

    @Inject
    public OrderService(
            @Qualifier("razorpay") PaymentGateway gateway,
            EventPublisher eventPublisher) {
        this.gateway = Objects.requireNonNull(gateway);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
    }

    public void placeOrder() {
        placeOrder("order-demo-001");
    }

    public void placeOrder(String orderId) {
        gateway.pay();
        System.out.println("Order placed: " + orderId);
        eventPublisher.publishEvent(new OrderCreated(orderId));
    }
}
