
package com.minispring.aop;

import java.util.Objects;

public class InterceptorBinding {

    private final MethodMatcher matcher;
    private final MethodInterceptor interceptor;

    public InterceptorBinding(
            MethodMatcher matcher,
            MethodInterceptor interceptor) {

        this.matcher = Objects.requireNonNull(
                matcher, "matcher must not be null");

        this.interceptor = Objects.requireNonNull(
                interceptor, "interceptor must not be null");
    }

    public MethodMatcher getMatcher() {
        return matcher;
    }

    public MethodInterceptor getInterceptor() {
        return interceptor;
    }
}
