
package com.minispring.aop;

import com.minispring.lifecycle.BeanPostProcessor;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class AopBeanPostProcessor implements BeanPostProcessor {

    private final List<InterceptorBinding> bindings;
    private final MiniProxyFactory proxyFactory;

    public AopBeanPostProcessor(
            List<InterceptorBinding> bindings) {

        Objects.requireNonNull(
                bindings, "bindings must not be null");

        this.bindings = List.copyOf(bindings);

        MethodExecution execution =
                new MethodExecution(this.bindings);

        this.proxyFactory = new MiniProxyFactory(execution);
    }

    @Override
    public Object beforeInitialization(Object bean) {
        return bean;
    }

    @Override
    public Object afterInitialization(Object bean) {

        if (bean == null || bindings.isEmpty()) {
            return bean;
        }

        // Avoid wrapping an existing dynamic proxy again.
        if (Proxy.isProxyClass(bean.getClass())) {
            return bean;
        }

        Class<?>[] interfaces = findInterfaces(bean.getClass());

        // This version supports interface-based proxies.
        if (interfaces.length == 0) {
            return bean;
        }

        // Don't proxy beans unless at least one visible interface
        // method matches a configured binding.
        if (!hasMatchingMethod(interfaces)) {
            return bean;
        }

        return proxyFactory.createProxy(bean, interfaces);
    }

    private boolean hasMatchingMethod(Class<?>[] interfaces) {

        for (Class<?> interfaceType : interfaces) {
            for (Method method : interfaceType.getMethods()) {
                for (InterceptorBinding binding : bindings) {
                    if (binding.getMatcher().matches(method)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private Class<?>[] findInterfaces(Class<?> targetType) {

        Set<Class<?>> interfaces = new LinkedHashSet<>();

        for (Class<?> type = targetType;
             type != null;
             type = type.getSuperclass()) {

            for (Class<?> interfaceType : type.getInterfaces()) {
                collectInterfaceHierarchy(interfaceType, interfaces);
            }
        }

        return new ArrayList<>(interfaces).toArray(Class<?>[]::new);
    }

    private void collectInterfaceHierarchy(
            Class<?> interfaceType,
            Set<Class<?>> interfaces) {

        if (!interfaces.add(interfaceType)) {
            return;
        }

        for (Class<?> parent : interfaceType.getInterfaces()) {
            collectInterfaceHierarchy(parent, interfaces);
        }
    }
}
