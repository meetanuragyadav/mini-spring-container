package com.minispring.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Restricts a component to one or more active profiles.
 *
 * <p>A component annotated with {@code @Profile("dev")} is
 * eligible only when the {@code dev} profile is active.</p>
 *
 * <p>For this educational container, multiple profiles use
 * OR semantics: at least one declared profile must be active.</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Profile {

    String[] value();
}
