package com.minispring.demo;

import com.minispring.context.ApplicationContext;
import com.minispring.context.Environment;

/**
 * Small executable demonstration of the current MiniSpring feature set.
 */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) throws Exception {
        Environment environment = new Environment();
        environment.addProfile("dev");
        environment.setProperty("feature.enabled", "true");

        try (ApplicationContext context =
                     new ApplicationContext("com.minispring.demo", environment)) {

            OrderService orderService =
                    context.getBean(OrderService.class);
            orderService.placeOrder();

            ScopeConsumer scopeConsumer =
                    context.getBean(ScopeConsumer.class);

            PrototypeTest first = scopeConsumer.getPrototype();
            PrototypeTest second = scopeConsumer.getPrototype();

            System.out.println(
                    "Prototype instances are different: "
                            + (first != second)
            );

            FactoryArgumentTest factoryBean =
                    context.getBean(FactoryArgumentTest.class);
            factoryBean.test();
        }
    }
}
