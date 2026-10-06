package com.minispring.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class that declares {@link Bean} factory methods.
 *
 * The configuration class itself is registered as a managed bean because
 * non-static factory methods need an instance on which to be invoked.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Configuration {
}
