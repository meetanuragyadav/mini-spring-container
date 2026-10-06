package com.minispring.demo;

import com.minispring.annotation.*;
import com.minispring.context.*;
import com.minispring.core.*;
import com.minispring.lifecycle.*;

/**
 * Example service that depends on an interface rather than a concrete payment
 * implementation. The constructor demonstrates qualifier-based selection.
 */
@Component
public class OrderService {

    private final PaymentGateway gateway;

    @Inject
    public OrderService(
            @Qualifier("razorpay")
            PaymentGateway gateway) {

        this.gateway = gateway;
    }

    public void placeOrder() {

        gateway.pay();

        System.out.println("Order placed");
    }
}
