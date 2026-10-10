
package com.minispring.aop;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InterceptorChainTest {

    static class PriceService {

        public double calculatePrice() {
            System.out.println("Calculating price...");
            return 499.0;
        }

        public double failToCalculatePrice() {
            throw new IllegalStateException(
                    "Price calculation failed"
            );
        }
    }

    @Test
    void shouldRunLoggingAroundRealMethodAndPreserveResult()
            throws Throwable {

        // Arrange: prepare the real service and its method.
        PriceService target = new PriceService();

        Method method = PriceService.class
                .getMethod("calculatePrice");

        InterceptorChain chain = new InterceptorChain(
                List.of(new LoggingInterceptor())
        );

        // Capture console output so we can verify execution order.
        String output = captureOutput(() -> {
            Object result = chain.execute(target, method);

            assertEquals(499.0, (Double) result, 0.0001);
        });

        // The log must surround the real method execution.
        int beforeIndex =
                output.indexOf("[LOG] Before calculatePrice");
        int methodIndex =
                output.indexOf("Calculating price...");
        int afterIndex =
                output.indexOf("[LOG] After calculatePrice");

        assertTrue(beforeIndex >= 0, "Before log should appear");
        assertTrue(methodIndex >= 0, "Real method should execute");
        assertTrue(afterIndex >= 0, "After log should appear");

        assertTrue(beforeIndex < methodIndex);
        assertTrue(methodIndex < afterIndex);
    }

    @Test
    void shouldPropagateOriginalExceptionFromTarget()
            throws Exception {

        PriceService target = new PriceService();

        Method method = PriceService.class
                .getMethod("failToCalculatePrice");

        InterceptorChain chain = new InterceptorChain(
                List.of(new LoggingInterceptor())
        );

        // The original exception should reach the caller.
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> chain.execute(target, method)
        );

        assertEquals(
                "Price calculation failed",
                exception.getMessage()
        );
    }

    @Test
    void shouldRunAfterLogWhenTargetThrows()
            throws Throwable  {

        PriceService target = new PriceService();

        Method method = PriceService.class
                .getMethod("failToCalculatePrice");

        InterceptorChain chain = new InterceptorChain(
                List.of(new LoggingInterceptor())
        );

        String output = captureOutput(() -> {
            assertThrows(
                    IllegalStateException.class,
                    () -> chain.execute(target, method)
            );
        });

        assertTrue(
                output.contains("[LOG] Before failToCalculatePrice")
        );

        assertTrue(
                output.contains("[LOG] After failToCalculatePrice")
        );
    }

    @Test
    void shouldExecuteMultipleInterceptorsInNestedOrder()
            throws Throwable {

        PriceService target = new PriceService();

        Method method = PriceService.class
                .getMethod("calculatePrice");

        // The first interceptor is the outer behavior.
        InterceptorChain chain = new InterceptorChain(
                List.of(
                        new TimingInterceptor(),
                        new LoggingInterceptor()
                )
        );

        String output = captureOutput(() -> {
            Object result = chain.execute(target, method);

            assertEquals(499.0, (Double) result, 0.0001);
        });

        int loggingBeforeIndex =
                output.indexOf("[LOG] Before calculatePrice");
        int methodIndex =
                output.indexOf("Calculating price...");
        int loggingAfterIndex =
                output.indexOf("[LOG] After calculatePrice");
        int timingIndex =
                output.indexOf("[TIME] calculatePrice took");

        assertTrue(loggingBeforeIndex >= 0);
        assertTrue(methodIndex >= 0);
        assertTrue(loggingAfterIndex >= 0);
        assertTrue(timingIndex >= 0);

        // Logging surrounds the real method.
        assertTrue(loggingBeforeIndex < methodIndex);
        assertTrue(methodIndex < loggingAfterIndex);

        // Timing finishes after the inner logging behavior.
        assertTrue(loggingAfterIndex < timingIndex);
    }

    @Test
    void shouldRejectSecondProceedCallWithoutExecutingTargetTwice()
            throws Throwable {

        // Arrange: a target that counts its executions.
        class CounterService {
            private int calls;

            public int increment() {
                calls++;
                return calls;
            }
        }

        CounterService target = new CounterService();

        Method method = CounterService.class.getMethod("increment");

        // This interceptor deliberately makes the same continuation
        // twice to simulate a programming mistake.
        MethodInterceptor doubleProceedInterceptor =
                new MethodInterceptor() {
                    @Override
                    public Object execute(Invocation invocation)
                            throws Throwable {

                        invocation.proceed();
                        return invocation.proceed();
                    }
                };

        InterceptorChain chain = new InterceptorChain(
                List.of(doubleProceedInterceptor));

        // Act + Assert: the second continuation must fail.
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> chain.execute(target, method));

        assertTrue(exception.getMessage().contains(
                "already proceeded"));

        // The first call executed the real method exactly once.
        assertEquals(1, target.calls);
    }

    @FunctionalInterface
    private interface TestAction {
        void run() throws Throwable;
    }

    private static String captureOutput(TestAction action)
            throws Throwable {

        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try (PrintStream capturedOut = new PrintStream(
                output, true, StandardCharsets.UTF_8)) {

            System.setOut(capturedOut);
            action.run();

        } finally {
            System.setOut(originalOut);
        }

        return output.toString(StandardCharsets.UTF_8);
    }
}
