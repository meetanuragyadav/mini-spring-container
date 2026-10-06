package com.minispring.scanner;

import com.minispring.annotation.*;

import java.lang.reflect.Method;

/**
 * Discovery result for one @Bean method.
 *
 * Keeping this as a small metadata object prevents the scanners from knowing
 * anything about runtime registration or bean creation.
 */
public class BeanMethod {

    private final Class<?> configurationClass;
    private final Method method;

    public BeanMethod(
            Class<?> configurationClass,
            Method method) {
        this.configurationClass = configurationClass;
        this.method = method;
    }

    public Class<?> getConfigurationClass() {
        return configurationClass;
    }

    public Method getMethod() {
        return method;
    }
}
