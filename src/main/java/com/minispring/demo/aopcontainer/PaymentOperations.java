package com.minispring.demo.aopcontainer;

/** Business contract used by the container-integrated AOP example. */
public interface PaymentOperations {
    String processPayment(String orderId);
}
