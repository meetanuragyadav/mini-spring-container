
package com.minispring.aop;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MethodExecution {

    private final List<InterceptorBinding> bindings;

    public MethodExecution(List<InterceptorBinding> bindings) {
        Objects.requireNonNull(bindings, "bindings must not be null");
        this.bindings = List.copyOf(bindings);
    }

    public Object execute(
            Object target,
            Method method,
            Object... arguments) throws Throwable {

        Objects.requireNonNull(target, "target must not be null");
        Objects.requireNonNull(method, "method must not be null");

        List<MethodInterceptor> selectedInterceptors =
                new ArrayList<>();

        for (InterceptorBinding binding : bindings) {
            if (binding.getMatcher().matches(method)) {
                selectedInterceptors.add(
                        binding.getInterceptor());
            }
        }

        InterceptorChain chain =
                new InterceptorChain(selectedInterceptors);

        return chain.execute(target, method, arguments);
    }
}
