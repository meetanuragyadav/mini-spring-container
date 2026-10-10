package com.minispring.demo.aop;

public class OrderService implements OrderOperations {

    @Override
    public void placeOrder() {
        System.out.println("Placing order...");
    }
}