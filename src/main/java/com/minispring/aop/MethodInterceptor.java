
package com.minispring.aop;

public interface MethodInterceptor {

    Object execute(Invocation invocation) throws Throwable;
}
