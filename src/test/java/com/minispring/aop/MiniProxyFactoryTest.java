
package com.minispring.aop;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class MiniProxyFactoryTest {

    interface OrderService {
        String saveOrder();
        String cancelOrder();
    }

    static class OrderServiceImpl implements OrderService {

        @Override
        public String saveOrder() {
            return "order saved";
        }

        @Override
        public String cancelOrder() {
            return "order cancelled";
        }
    }

    @Test
    void shouldReturnAnObjectImplementingTheInterface() {
        MethodExecution execution = new MethodExecution(List.of());
        MiniProxyFactory factory = new MiniProxyFactory(execution);

        OrderService target = new OrderServiceImpl();
        OrderService proxy = factory.createProxy(
                target, OrderService.class);

        assertNotNull(proxy);
        assertTrue(proxy instanceof OrderService);
        assertTrue(java.lang.reflect.Proxy.isProxyClass(
                proxy.getClass()));
    }

    @Test
    void shouldInterceptSelectedMethod() {
        AtomicInteger interceptorCalls = new AtomicInteger();

        MethodInterceptor interceptor = invocation -> {
            interceptorCalls.incrementAndGet();
            return invocation.proceed();
        };

        MethodExecution execution = new MethodExecution(List.of(
                new InterceptorBinding(
                        new MethodNameMatcher("saveOrder"),
                        interceptor)
        ));

        MiniProxyFactory factory = new MiniProxyFactory(execution);
        OrderService proxy = factory.createProxy(
                new OrderServiceImpl(), OrderService.class);

        assertEquals("order saved", proxy.saveOrder());
        assertEquals(1, interceptorCalls.get());
    }

    @Test
    void shouldNotInterceptUnselectedMethod() {
        AtomicInteger interceptorCalls = new AtomicInteger();

        MethodInterceptor interceptor = invocation -> {
            interceptorCalls.incrementAndGet();
            return invocation.proceed();
        };

        MethodExecution execution = new MethodExecution(List.of(
                new InterceptorBinding(
                        new MethodNameMatcher("saveOrder"),
                        interceptor)
        ));

        MiniProxyFactory factory = new MiniProxyFactory(execution);
        OrderService proxy = factory.createProxy(
                new OrderServiceImpl(), OrderService.class);

        assertEquals("order cancelled", proxy.cancelOrder());
        assertEquals(0, interceptorCalls.get());
    }

    @Test
    void shouldRejectAConcreteClassAsProxyType() {
        MiniProxyFactory factory = new MiniProxyFactory(
                new MethodExecution(List.of()));

        assertThrows(
                IllegalArgumentException.class,
                () -> factory.createProxy(
                        new OrderServiceImpl(),
                        OrderServiceImpl.class));
    }

    @Test
    void shouldRejectTargetThatDoesNotImplementInterface() {
        MiniProxyFactory factory = new MiniProxyFactory(
                new MethodExecution(List.of()));

        assertThrows(
                IllegalArgumentException.class,
                () -> factory.createProxy(
                        "not an order service",
                        OrderService.class));
    }
}
