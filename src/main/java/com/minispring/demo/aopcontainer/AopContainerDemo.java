package com.minispring.demo.aopcontainer;

import com.minispring.aop.InterceptorBinding;
import com.minispring.aop.LoggingInterceptor;
import com.minispring.aop.MethodNameMatcher;
import com.minispring.aop.TimingInterceptor;
import com.minispring.context.ApplicationContext;
import com.minispring.context.Environment;

import java.util.List;

/** Demonstrates AOP proxy creation through ApplicationContext. */
public final class AopContainerDemo {

    private AopContainerDemo() {
    }

    public static void main(String[] args) throws Exception {
        List<InterceptorBinding> bindings = List.of(
                new InterceptorBinding(
                        new MethodNameMatcher("processPayment"),
                        new LoggingInterceptor()),
                new InterceptorBinding(
                        new MethodNameMatcher("processPayment"),
                        new TimingInterceptor())
        );

        try (ApplicationContext context = new ApplicationContext(
                "com.minispring.demo.aopcontainer",
                new Environment(),
                bindings)) {
            PaymentOperations payments =
                    context.getBean(PaymentOperations.class);
            String result = payments.processPayment("order-1001");
            System.out.println("[CALLER] " + result);
        }
    }
}
