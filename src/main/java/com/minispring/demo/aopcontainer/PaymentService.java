package com.minispring.demo.aopcontainer;

import com.minispring.annotation.Component;

/** Example business service that can be wrapped by the AOP post-processor. */
@Component
public class PaymentService implements PaymentOperations {

    @Override
    public String processPayment(String orderId) {
        System.out.println("[BUSINESS] Processing payment for " + orderId);
        return "Payment accepted for " + orderId;
    }
}
