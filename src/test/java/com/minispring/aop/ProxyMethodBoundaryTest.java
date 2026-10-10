
package com.minispring.aop;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class ProxyMethodBoundaryTest {

    interface OrderService {
        String findOrder(String orderId);

        void deleteOrder(String orderId);

        String fail(String message);

        String echo(String value);
    }

    static class OrderServiceImpl implements OrderService {

        private final AtomicInteger deleteCalls = new AtomicInteger();

        @Override
        public String findOrder(String orderId) {
            return "Found: " + orderId;
        }

        @Override
        public void deleteOrder(String orderId) {
            deleteCalls.incrementAndGet();
        }

        @Override
        public String fail(String message) {
            throw new IllegalStateException(message);
        }

        @Override
        public String echo(String value) {
            return value;
        }
    }

    @Test
    void shouldPreserveArgumentsAndReturnValue() {
        OrderService proxy = createProxy(new OrderServiceImpl());

        String result = proxy.findOrder("order-42");

        assertEquals("Found: order-42", result);
    }

    @Test
    void shouldSupportNullArguments() {
        OrderService proxy = createProxy(new OrderServiceImpl());

        assertNull(proxy.echo(null));
    }

    @Test
    void shouldExecuteVoidMethod() {
        OrderServiceImpl target = new OrderServiceImpl();
        OrderService proxy = createProxy(target);

        assertDoesNotThrow(() -> proxy.deleteOrder("order-42"));
        assertEquals(1, target.deleteCalls.get());
    }

    @Test
    void shouldPropagateOriginalRuntimeException() {
        OrderService proxy = createProxy(new OrderServiceImpl());

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> proxy.fail("database unavailable"));

        assertEquals("database unavailable", exception.getMessage());
    }

    @Test
    void shouldApplyInterceptorWithoutChangingResult() {
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

        assertEquals("Found: order-42", proxy.findOrder("order-42"));
        assertEquals(1, interceptorCalls.get());
    }

    private static OrderService createProxy(OrderServiceImpl target) {
        MethodExecution execution = new MethodExecution(List.of());
        MiniProxyFactory factory = new MiniProxyFactory(execution);

        return factory.createProxy(target, OrderService.class);
    }
}
