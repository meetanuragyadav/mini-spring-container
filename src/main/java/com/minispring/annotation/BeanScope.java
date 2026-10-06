package com.minispring.annotation;
import com.minispring.core.Scope;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares the lifecycle scope of a component or @Bean method.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface BeanScope {

    Scope value() default Scope.SINGLETON;
}
