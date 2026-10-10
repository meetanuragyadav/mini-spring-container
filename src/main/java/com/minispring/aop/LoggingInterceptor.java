
package com.minispring.aop;

public class LoggingInterceptor implements MethodInterceptor {

    @Override
    public Object execute(Invocation invocation) throws Throwable {
        System.out.println(
                "[LOG] Before " + invocation.getMethod().getName()
        );

        try {
            return invocation.proceed();
        } finally {
            System.out.println(
                    "[LOG] After " + invocation.getMethod().getName()
            );
        }
    }
}
