
package com.minispring.aop;

public class TimingInterceptor implements MethodInterceptor {

    @Override
    public Object execute(Invocation invocation) throws Throwable {
        long start = System.nanoTime();

        try {
            return invocation.proceed();
        } finally {
            long elapsed = System.nanoTime() - start;

            System.out.println(
                    "[TIME] " + invocation.getMethod().getName()
                            + " took " + elapsed + " ns"
            );
        }
    }
}
