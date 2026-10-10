
package com.minispring.aop;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class ProxyObjectMethodsTest {

    interface OrderService {
        String saveOrder();
    }

    static class OrderServiceImpl implements OrderService {
        @Override
        public String saveOrder() {
            return "order saved";
        }
    }

    @Test
    void proxyShouldBeEqualToItself() {
        OrderService proxy = createProxyWithAllMethodsSelected();

        assertTrue(proxy.equals(proxy));
    }

    @Test
    void proxyShouldNotBeEqualToItsRealTarget() {
        OrderService target = new OrderServiceImpl();
        OrderService proxy = createProxy(target);

        assertFalse(proxy.equals(target));
    }

    @Test
    void proxyShouldHaveStableHashCode() {
        OrderService proxy = createProxyWithAllMethodsSelected();

        int first = proxy.hashCode();
        int second = proxy.hashCode();

        assertEquals(first, second);
    }

    @Test
    void objectMethodsShouldNotRunBusinessInterceptors() {
        AtomicInteger interceptorCalls = new AtomicInteger();

        MethodInterceptor interceptor = invocation -> {
            interceptorCalls.incrementAndGet();
            return invocation.proceed();
        };

        MethodExecution execution = new MethodExecution(List.of(
                new InterceptorBinding(method -> true, interceptor)
        ));

        MiniProxyFactory factory = new MiniProxyFactory(execution);
        OrderService proxy = factory.createProxy(
                new OrderServiceImpl(), OrderService.class);

        proxy.toString();
        proxy.equals(proxy);
        proxy.hashCode();

        assertEquals(0, interceptorCalls.get());

        proxy.saveOrder();

        assertEquals(1, interceptorCalls.get());
    }

    private static OrderService createProxyWithAllMethodsSelected() {
        return createProxy(new OrderServiceImpl());
    }

    private static OrderService createProxy(OrderService target) {
        MethodExecution execution = new MethodExecution(List.of());
        MiniProxyFactory factory = new MiniProxyFactory(execution);
        return factory.createProxy(target, OrderService.class);
    }
}
