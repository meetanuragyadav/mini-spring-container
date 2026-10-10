package com.minispring.demo.aop;

public class LoggingOrderProxy implements OrderOperations {

    private final OrderOperations target;

    public LoggingOrderProxy(OrderOperations target) {
        this.target = target;
    }

    @Override
    public void placeOrder() {
        System.out.println("[LOG] Before placeOrder");

        try {
            target.placeOrder();
        } finally {
            System.out.println("[LOG] After placeOrder");
        }
    }
}