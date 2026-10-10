
package com.minispring.aop;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class AopHardeningTest {

    interface PriceService {
        int calculate(int quantity);
    }

    interface HealthCheck {
        boolean healthy();
    }

    static class StoreService implements PriceService, HealthCheck {
        @Override
        public int calculate(int quantity) {
            return quantity * 100;
        }

        @Override
        public boolean healthy() {
            return true;
        }
    }

    interface FileService {
        String read();
    }

    static class FileServiceImpl implements FileService {
        @Override
        public String read() {
            throw new RuntimeException(
                    new IOException("disk unavailable"));
        }
    }

    @Test
    void shouldPreservePrimitiveReturnValues() {
        MiniProxyFactory factory = new MiniProxyFactory(
                new MethodExecution(List.of()));

        PriceService proxy = factory.createProxy(
                new StoreService(), PriceService.class);

        assertEquals(500, proxy.calculate(5));
    }

    @Test
    void shouldPreserveAllConfiguredInterfaces() {
        StoreService target = new StoreService();

        MiniProxyFactory factory = new MiniProxyFactory(
                new MethodExecution(List.of()));

        Object proxy = factory.createProxy(
                target,
                new Class<?>[]{
                        PriceService.class,
                        HealthCheck.class
                });

        assertTrue(proxy instanceof PriceService);
        assertTrue(proxy instanceof HealthCheck);
        assertEquals(300, ((PriceService) proxy).calculate(3));
        assertTrue(((HealthCheck) proxy).healthy());
    }

    @Test
    void shouldPropagateRuntimeExceptionWithoutReflectionWrapper() {
        MiniProxyFactory factory = new MiniProxyFactory(
                new MethodExecution(List.of()));

        FileService proxy = factory.createProxy(
                new FileServiceImpl(), FileService.class);

        RuntimeException exception = assertThrows(
                RuntimeException.class, proxy::read);

        assertInstanceOf(IOException.class, exception.getCause());
        assertEquals("disk unavailable",
                exception.getCause().getMessage());
    }

    @Test
    void shouldKeepInterceptorOrderDeterministic() throws Throwable {
        StringBuilder order = new StringBuilder();

        MethodInterceptor first = invocation -> {
            order.append("A>");
            Object result = invocation.proceed();
            order.append("<A");
            return result;
        };

        MethodInterceptor second = invocation -> {
            order.append("B>");
            Object result = invocation.proceed();
            order.append("<B");
            return result;
        };

        MethodExecution execution = new MethodExecution(List.of(
                new InterceptorBinding(
                        method -> method.getName().equals("calculate"),
                        first),
                new InterceptorBinding(
                        method -> method.getName().equals("calculate"),
                        second)
        ));

        PriceService proxy = new MiniProxyFactory(execution)
                .createProxy(new StoreService(), PriceService.class);

        assertEquals(200, proxy.calculate(2));
        assertEquals("A>B><B<A", order.toString());
    }

    @Test
    void shouldSupportConcurrentCalls() throws Exception {
        AtomicInteger interceptorCalls = new AtomicInteger();

        MethodInterceptor countingInterceptor = invocation -> {
            interceptorCalls.incrementAndGet();
            return invocation.proceed();
        };

        MethodExecution execution = new MethodExecution(List.of(
                new InterceptorBinding(
                        method -> method.getName().equals("calculate"),
                        countingInterceptor)
        ));

        PriceService proxy = new MiniProxyFactory(execution)
                .createProxy(new StoreService(), PriceService.class);

        int workers = 8;
        int callsPerWorker = 100;

        ExecutorService executor =
                Executors.newFixedThreadPool(workers);

        try {
            List<Future<?>> futures = new java.util.ArrayList<>();

            for (int worker = 0; worker < workers; worker++) {
                futures.add(executor.submit(() -> {
                    for (int i = 0; i < callsPerWorker; i++) {
                        assertEquals(100, proxy.calculate(1));
                    }
                }));
            }

            for (Future<?> future : futures) {
                future.get();
            }

            assertEquals(workers * callsPerWorker,
                    interceptorCalls.get());
        } finally {
            executor.shutdownNow();
        }
    }
}
