
package com.minispring.aop;

import java.lang.reflect.Method;
import java.util.List;

public class InterceptorChain {

    private final List<MethodInterceptor> interceptors;

    public InterceptorChain(List<MethodInterceptor> interceptors) {
        this.interceptors = List.copyOf(interceptors);
    }

    public Object execute(
            Object target,
            Method method,
            Object... arguments
    ) throws Throwable {

        Invocation invocation = new Invocation(
                target,
                method,
                arguments,
                interceptors,
                0
        );

        return invocation.proceed();
    }
}
