package com.minispring.demo;

import com.minispring.annotation.*;
import com.minispring.context.*;
import com.minispring.core.*;
import com.minispring.lifecycle.*;

/**
 * Demonstrates dependency injection into an {@link Bean} factory method.
 */
public class FactoryArgumentTest {

    private final Logger logger;

    public FactoryArgumentTest(Logger logger) {
        this.logger = logger;
    }

    public void test() {
        System.out.println(
                "Factory argument received: "
                        + logger.getClass().getName()
        );
    }
}
