package com.minispring.events;

import java.lang.reflect.Method;
import java.util.Objects;

public final class EventListenerDefinition {

    private final Class<?> beanClass;
    private final Method method;
    private final Class<?> eventType;

    public EventListenerDefinition(
            Class<?> beanClass,
            Method method,
            Class<?> eventType) {

        this.beanClass = Objects.requireNonNull(beanClass);
        this.method = Objects.requireNonNull(method);
        this.eventType = Objects.requireNonNull(eventType);
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public Method getMethod() {
        return method;
    }

    public Class<?> getEventType() {
        return eventType;
    }
}