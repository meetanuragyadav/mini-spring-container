 package com.minispring.demo.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;

public class ProxyDemo {

    public static void main(String[] args) {

        // The real object
        OrderOperations target = new OrderService();

        // Logic that runs whenever a proxy method is called
        InvocationHandler handler = (proxy, method, arguments) -> {

            System.out.println(
                    "[LOG] Before " + method.getName()
            );

            try {
                // Forward the call to the real object
                return method.invoke(target, arguments);

            } catch (InvocationTargetException exception) {
                // Preserve the exception thrown by the real method
                throw exception.getCause();

            } finally {
                System.out.println(
                        "[LOG] After " + method.getName()
                );
            }
        };

        // Ask Java to generate an object implementing the interface
        OrderOperations proxy = (OrderOperations)
                Proxy.newProxyInstance(
                        OrderOperations.class.getClassLoader(),
                        new Class<?>[]{OrderOperations.class},
                        handler
                );

        // The caller uses the proxy just like a normal service
        proxy.placeOrder();
    }
}