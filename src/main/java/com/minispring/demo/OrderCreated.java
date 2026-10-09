package com.minispring.demo;

import java.util.Objects;

public record OrderCreated(String orderId) {

    public OrderCreated {
        Objects.requireNonNull(orderId, "orderId must not be null");
    }
}