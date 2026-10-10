
package com.minispring.demo.aop;

import com.minispring.aop.InterceptorChain;
import com.minispring.aop.LoggingInterceptor;
import com.minispring.aop.TimingInterceptor;

import java.lang.reflect.Method;
import java.util.List;

public class InterceptorChainDemo {

    public static class PriceService {

        public double calculatePrice(String product) {
            System.out.println(
                    "[BUSINESS] Calculating price for " + product
            );

            return 499.0;
        }

        public double calculatePriceWithFailure() {
            System.out.println(
                    "[BUSINESS] Starting price calculation..."
            );

            throw new IllegalStateException(
                    "Unable to calculate price"
            );
        }
    }

    public static void main(String[] args) throws Throwable {

        PriceService target = new PriceService();

        InterceptorChain chain = new InterceptorChain(
                List.of(
                        new TimingInterceptor(),
                        new LoggingInterceptor()
                )
        );

        System.out.println("=== Successful call ===");

        Method calculateMethod = PriceService.class.getMethod(
                "calculatePrice",
                String.class
        );

        Object result = chain.execute(
                target,
                calculateMethod,
                "Laptop"
        );

        System.out.println("[CALLER] Result: " + result);

        System.out.println();
        System.out.println("=== Failing call ===");

        Method failingMethod = PriceService.class.getMethod(
                "calculatePriceWithFailure"
        );

        try {
            chain.execute(target, failingMethod);
        } catch (IllegalStateException exception) {
            System.out.println(
                    "[CALLER] Caught: " + exception.getMessage()
            );
        }
    }
}
