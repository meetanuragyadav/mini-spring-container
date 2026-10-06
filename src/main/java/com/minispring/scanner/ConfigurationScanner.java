package com.minispring.scanner;

import com.minispring.annotation.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Discovers @Bean factory methods inside @Configuration classes.
 *
 * The scanner returns BeanMethod metadata. It does not invoke the methods;
 * actual invocation belongs to Container during bean creation.
 */
public class ConfigurationScanner {

    public List<BeanMethod> scan(List<Class<?>> classes) {

        List<BeanMethod> beanMethods = new ArrayList<>();

        for (Class<?> clazz : classes) {
            if (!clazz.isAnnotationPresent(Configuration.class)) {
                continue;
            }

            for (Method method : clazz.getDeclaredMethods()) {
                if (!method.isAnnotationPresent(Bean.class)) {
                    continue;
                }

                beanMethods.add(
                        new BeanMethod(clazz, method)
                );
            }
        }

        return beanMethods;
    }
}
