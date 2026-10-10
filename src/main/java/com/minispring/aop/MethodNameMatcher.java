
package com.minispring.aop;

import java.lang.reflect.Method;
import java.util.Objects;

public class MethodNameMatcher implements MethodMatcher {

    private final String methodName;

    public MethodNameMatcher(String methodName) {
        this.methodName = Objects.requireNonNull(
                methodName, "methodName must not be null");
    }

    @Override
    public boolean matches(Method method) {
        return method.getName().equals(methodName);
    }
}
