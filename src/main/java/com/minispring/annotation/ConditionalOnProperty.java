package com.minispring.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Makes a component or @Bean method conditional on a configuration property.
 *
 * <p>The definition is eligible when the named property exists and its value
 * equals {@link #havingValue()}.</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface ConditionalOnProperty {

    String name();

    String havingValue();
}
