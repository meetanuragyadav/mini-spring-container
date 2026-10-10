
package com.minispring.aop;

import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Objects;

public class MiniProxyFactory {

    private final MethodExecution methodExecution;

    public MiniProxyFactory(MethodExecution methodExecution) {
        this.methodExecution = Objects.requireNonNull(
                methodExecution,
                "methodExecution must not be null");
    }

    public <T> T createProxy(Object target, Class<T> interfaceType) {
        Objects.requireNonNull(
                interfaceType, "interfaceType must not be null");

        Object proxy = createProxyObject(
                target, new Class<?>[]{interfaceType});

        return interfaceType.cast(proxy);
    }

    public Object createProxy(
            Object target, Class<?>[] interfaceTypes) {

        return createProxyObject(target, interfaceTypes);
    }

    private Object createProxyObject(
            Object target, Class<?>[] interfaceTypes) {

        Objects.requireNonNull(target, "target must not be null");
        Objects.requireNonNull(
                interfaceTypes, "interfaceTypes must not be null");

        if (interfaceTypes.length == 0) {
            throw new IllegalArgumentException(
                    "At least one interface is required");
        }

        Class<?>[] interfaces = Arrays.copyOf(
                interfaceTypes, interfaceTypes.length);

        for (Class<?> interfaceType : interfaces) {
            Objects.requireNonNull(
                    interfaceType, "interface type must not be null");

            if (!interfaceType.isInterface()) {
                throw new IllegalArgumentException(
                        "Proxy type must be an interface: "
                                + interfaceType.getName());
            }

            if (!interfaceType.isInstance(target)) {
                throw new IllegalArgumentException(
                        "Target does not implement "
                                + interfaceType.getName());
            }
        }

        return Proxy.newProxyInstance(
                interfaces[0].getClassLoader(),
                interfaces,
                (proxyObject, method, arguments) -> {

                    if (method.getDeclaringClass() == Object.class) {
                        return switch (method.getName()) {
                            case "equals" ->
                                    proxyObject == arguments[0];

                            case "hashCode" ->
                                    System.identityHashCode(proxyObject);

                            case "toString" ->
                                    "MiniProxy for "
                                            + target.getClass().getName();

                            default -> throw new UnsupportedOperationException(
                                    "Unsupported Object method: "
                                            + method.getName());
                        };
                    }

                    return methodExecution.execute(
                            target, method, arguments);
                }
        );
    }
}
