
package com.minispring.aop;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class Invocation {

    private final Object target;
    private final Method method;
    private final Object[] arguments;
    private final List<MethodInterceptor> interceptors;
    private final int position;

    // Tracks whether this invocation has already continued.
    private boolean proceeded;

    public Invocation(
            Object target,
            Method method,
            Object[] arguments,
            List<MethodInterceptor> interceptors,
            int position) {

        this.target = target;
        this.method = method;
        this.arguments = arguments;
        this.interceptors = interceptors;
        this.position = position;
        this.proceeded = false;
    }

    public Object getTarget() {
        return target;
    }

    public Method getMethod() {
        return method;
    }

    public Object[] getArguments() {
        return arguments;
    }

    public Object proceed() throws Throwable {

        // Reject a second continuation of this same invocation.
        if (proceeded) {
            throw new IllegalStateException(
                    "This invocation has already proceeded: "
                            + method.getName());
        }

        // Mark it before executing anything further.
        proceeded = true;

        // No interceptors remain: invoke the real method.
        if (position >= interceptors.size()) {
            try {
                return method.invoke(target, arguments);
            } catch (InvocationTargetException exception) {
                throw exception.getCause();
            }
        }

        // Execute the current interceptor and give it
        // a continuation positioned at the next interceptor.
        MethodInterceptor current = interceptors.get(position);

        Invocation next = new Invocation(
                target,
                method,
                arguments,
                interceptors,
                position + 1);

        return current.execute(next);
    }
}
