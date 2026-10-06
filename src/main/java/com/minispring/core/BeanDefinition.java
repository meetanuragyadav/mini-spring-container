package com.minispring.core;
import com.minispring.condition.Condition;

import com.minispring.annotation.*;
import com.minispring.lifecycle.*;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Metadata describing how the container should manage one bean.
 *
 * <p>A BeanDefinition is deliberately not the bean itself. It is the bridge
 * between discovery/registration and runtime creation.</p>
 *
 * <p>Conditions are evaluated before the definition is registered with the
 * runtime Container. This keeps environment-specific decisions out of the
 * dependency-resolution engine.</p>
 */
public class BeanDefinition {

    private final Class<?> beanClass;
    private Scope scope;
    private final String qualifier;
    private final boolean primary;
    private Method factoryMethod;
    private final List<Condition> conditions = new ArrayList<>();

    public BeanDefinition(
            Class<?> beanClass,
            Scope scope,
            String qualifier,
            boolean primary) {

        this.beanClass = beanClass;
        this.scope = scope;
        this.qualifier = qualifier;
        this.primary = primary;
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public Scope getScope() {
        return scope;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public String getQualifier() {
        return qualifier;
    }

    public boolean isPrimary() {
        return primary;
    }

    /**
     * Non-null only when this definition represents an @Bean factory method.
     */
    public Method getFactoryMethod() {
        return factoryMethod;
    }

    public void setFactoryMethod(Method factoryMethod) {
        this.factoryMethod = factoryMethod;
    }

    /**
     * Add a condition that must match before this definition can be registered.
     */
    public void addCondition(Condition condition) {
        if (condition == null) {
            throw new IllegalArgumentException(
                    "Condition cannot be null"
            );
        }

        conditions.add(condition);
    }

    /**
     * Return the conditions attached to this definition.
     */
    public List<Condition> getConditions() {
        return Collections.unmodifiableList(conditions);
    }
}
