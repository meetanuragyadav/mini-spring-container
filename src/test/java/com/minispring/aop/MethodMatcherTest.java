
package com.minispring.aop;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class MethodMatcherTest {

    static class OrderService {
        public void saveOrder() {
        }

        public void cancelOrder() {
        }

        public void saveOrder(String orderId) {
        }
    }

    @Test
    void shouldMatchMethodWithRequestedName() throws Exception {
        Method method = OrderService.class.getMethod("saveOrder");

        MethodMatcher matcher = new MethodNameMatcher("saveOrder");

        assertTrue(matcher.matches(method));
    }

    @Test
    void shouldRejectMethodWithDifferentName() throws Exception {
        Method method = OrderService.class.getMethod("cancelOrder");

        MethodMatcher matcher = new MethodNameMatcher("saveOrder");

        assertFalse(matcher.matches(method));
    }

    @Test
    void shouldMatchBothOverloadsWithSameName() throws Exception {
        Method noArgumentMethod =
                OrderService.class.getMethod("saveOrder");

        Method oneArgumentMethod =
                OrderService.class.getMethod(
                        "saveOrder", String.class);

        MethodMatcher matcher = new MethodNameMatcher("saveOrder");

        assertTrue(matcher.matches(noArgumentMethod));
        assertTrue(matcher.matches(oneArgumentMethod));
    }

    @Test
    void shouldRejectNullMethodName() {
        assertThrows(
                NullPointerException.class,
                () -> new MethodNameMatcher(null));
    }
}
