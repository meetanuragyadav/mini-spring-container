
package com.minispring.aop.integration;

import com.minispring.annotation.Bean;
import com.minispring.annotation.Component;
import com.minispring.annotation.Configuration;
import com.minispring.aop.InterceptorBinding;
import com.minispring.aop.MethodInterceptor;
import com.minispring.aop.MethodNameMatcher;
import com.minispring.context.Environment;
import com.minispring.context.ApplicationContext;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class AopApplicationContextIntegrationTest {

    public interface OrderService {
        String placeOrder();
    }

    @Component
    public static class OrderServiceImpl implements OrderService {

        @Override
        public String placeOrder() {
            return "order placed";
        }
    }

    @Configuration
    public static class FactoryConfiguration {
        @Bean
        public FactoryOrderService factoryOrderService() {
            return new FactoryOrderServiceImpl();
        }
    }

    public interface FactoryOrderService {
        String submit();
    }

    public static class FactoryOrderServiceImpl implements FactoryOrderService {
        @Override
        public String submit() {
            return "factory order submitted";
        }
    }

    @Component
    public static class UnmatchedService {
        public String plainCall() {
            return "plain";
        }
    }

    @Component
    public static class OrderController {

        private final OrderService orderService;

        public OrderController(OrderService orderService) {
            this.orderService = orderService;
        }

        public String createOrder() {
            return orderService.placeOrder();
        }
    }

    @Test
    void shouldAutomaticallyProxyBeanResolvedByInterface()
            throws Exception {

        AtomicInteger interceptorCalls = new AtomicInteger();

        MethodInterceptor interceptor = invocation -> {
            interceptorCalls.incrementAndGet();
            return invocation.proceed();
        };

        List<InterceptorBinding> bindings = List.of(
                new InterceptorBinding(
                        new MethodNameMatcher("placeOrder"),
                        interceptor)
        );

        try (ApplicationContext context = new ApplicationContext(
                "com.minispring.aop.integration",
                new Environment(),
                bindings)) {

            OrderService service =
                    context.getBean(OrderService.class);

            assertTrue(
                    java.lang.reflect.Proxy.isProxyClass(
                            service.getClass()));

            assertEquals("order placed", service.placeOrder());
            assertEquals(1, interceptorCalls.get());
        }
    }

    @Test
    void shouldInjectProxiedServiceIntoAnotherBean()
            throws Exception {

        AtomicInteger interceptorCalls = new AtomicInteger();

        MethodInterceptor interceptor = invocation -> {
            interceptorCalls.incrementAndGet();
            return invocation.proceed();
        };

        List<InterceptorBinding> bindings = List.of(
                new InterceptorBinding(
                        new MethodNameMatcher("placeOrder"),
                        interceptor)
        );

        try (ApplicationContext context = new ApplicationContext(
                "com.minispring.aop.integration",
                new Environment(),
                bindings)) {

            OrderController controller =
                    context.getBean(OrderController.class);

            assertEquals("order placed", controller.createOrder());
            assertEquals(1, interceptorCalls.get());
        }
    }
    @Test
    void shouldReturnSameProxyForRepeatedSingletonLookups() throws Exception {
        List<InterceptorBinding> bindings = List.of(
                new InterceptorBinding(new MethodNameMatcher("placeOrder"),
                        invocation -> invocation.proceed()));

        try (ApplicationContext context = new ApplicationContext(
                "com.minispring.aop.integration", new Environment(), bindings)) {
            OrderService first = context.getBean(OrderService.class);
            OrderService second = context.getBean(OrderService.class);
            assertSame(first, second);
        }
    }

    @Test
    void shouldProxyBeanCreatedByBeanFactoryMethod() throws Exception {
        AtomicInteger calls = new AtomicInteger();
        List<InterceptorBinding> bindings = List.of(
                new InterceptorBinding(new MethodNameMatcher("submit"), invocation -> {
                    calls.incrementAndGet();
                    return invocation.proceed();
                }));

        try (ApplicationContext context = new ApplicationContext(
                "com.minispring.aop.integration", new Environment(), bindings)) {
            FactoryOrderService service = context.getBean(FactoryOrderService.class);
            assertTrue(java.lang.reflect.Proxy.isProxyClass(service.getClass()));
            assertEquals("factory order submitted", service.submit());
            assertEquals(1, calls.get());
        }
    }

    @Test
    void shouldLeaveUnmatchedBeanUnproxied() throws Exception {
        List<InterceptorBinding> bindings = List.of(
                new InterceptorBinding(new MethodNameMatcher("placeOrder"),
                        invocation -> invocation.proceed()));

        try (ApplicationContext context = new ApplicationContext(
                "com.minispring.aop.integration", new Environment(), bindings)) {
            UnmatchedService service = context.getBean(UnmatchedService.class);
            assertFalse(java.lang.reflect.Proxy.isProxyClass(service.getClass()));
            assertEquals("plain", service.plainCall());
        }
    }

}
