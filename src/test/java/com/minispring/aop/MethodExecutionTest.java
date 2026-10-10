
package com.minispring.aop;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class MethodExecutionTest {

    static class OrderService {

        public String saveOrder() {
            return "order saved";
        }

        public String cancelOrder() {
            return "order cancelled";
        }
    }

    @Test
    void shouldRunInterceptorForMatchingMethod() throws Throwable {
        OrderService target = new OrderService();
        AtomicInteger interceptorCalls = new AtomicInteger();

        MethodInterceptor countingInterceptor = invocation -> {
            interceptorCalls.incrementAndGet();
            return invocation.proceed();
        };

        MethodExecution execution = new MethodExecution(List.of(
                new InterceptorBinding(
                        new MethodNameMatcher("saveOrder"),
                        countingInterceptor)
        ));

        Method method = OrderService.class.getMethod("saveOrder");

        Object result = execution.execute(target, method);

        assertEquals("order saved", result);
        assertEquals(1, interceptorCalls.get());
    }

    @Test
    void shouldSkipInterceptorForNonMatchingMethod()
            throws Throwable {

        OrderService target = new OrderService();
        AtomicInteger interceptorCalls = new AtomicInteger();

        MethodInterceptor countingInterceptor = invocation -> {
            interceptorCalls.incrementAndGet();
            return invocation.proceed();
        };

        MethodExecution execution = new MethodExecution(List.of(
                new InterceptorBinding(
                        new MethodNameMatcher("saveOrder"),
                        countingInterceptor)
        ));

        Method method = OrderService.class.getMethod("cancelOrder");

        Object result = execution.execute(target, method);

        assertEquals("order cancelled", result);
        assertEquals(0, interceptorCalls.get());
    }

    @Test
    void shouldRunAllMatchingInterceptorsInConfigurationOrder()
            throws Throwable {

        OrderService target = new OrderService();
        StringBuilder order = new StringBuilder();

        MethodInterceptor first = invocation -> {
            order.append("first-before ");
            Object result = invocation.proceed();
            order.append("first-after ");
            return result;
        };

        MethodInterceptor second = invocation -> {
            order.append("second-before ");
            Object result = invocation.proceed();
            order.append("second-after ");
            return result;
        };

        MethodExecution execution = new MethodExecution(List.of(
                new InterceptorBinding(
                        new MethodNameMatcher("saveOrder"), first),
                new InterceptorBinding(
                        new MethodNameMatcher("saveOrder"), second)
        ));

        Method method = OrderService.class.getMethod("saveOrder");

        Object result = execution.execute(target, method);

        assertEquals("order saved", result);
        assertEquals(
                "first-before second-before second-after first-after ",
                order.toString());
    }
}
