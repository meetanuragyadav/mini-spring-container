package com.minispring.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method as a bean factory method.
 *
 * Methods carrying this annotation are discovered inside a class annotated
 * with {@link Configuration}. The method is not executed during discovery;
 * the container invokes it later when the bean is actually resolved.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Bean {
}
