package com.minispring.condition;

import com.minispring.context.Environment;

/**
 * Represents a condition that determines whether a bean
 * should participate in the current ApplicationContext.
 *
 * <p>A condition evaluates the current Environment and returns
 * whether the condition matches.</p>
 */
public interface Condition {

    /**
     * Determines whether this condition matches the environment.
     *
     * @param environment current application environment
     * @return true if the condition matches
     */
    boolean matches(Environment environment);
}
