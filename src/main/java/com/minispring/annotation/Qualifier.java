package com.minispring.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Gives a bean or injection point an explicit name used during candidate
 * selection.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({
        ElementType.TYPE,
        ElementType.METHOD,
        ElementType.PARAMETER
})
public @interface Qualifier {

    String value();
}
