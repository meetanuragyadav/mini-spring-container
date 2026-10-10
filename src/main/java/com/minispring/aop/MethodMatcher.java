
package com.minispring.aop;

import java.lang.reflect.Method;

@FunctionalInterface
public interface MethodMatcher {

    boolean matches(Method method);
}
