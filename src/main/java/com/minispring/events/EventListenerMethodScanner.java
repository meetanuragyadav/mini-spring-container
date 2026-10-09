package com.minispring.events;

import com.minispring.annotation.EventListener;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Discovers listener methods on a class already found by ComponentScanner. */
public final class EventListenerMethodScanner {

    public List<EventListenerDefinition> scan(Class<?> beanClass) {
        Objects.requireNonNull(beanClass, "Bean class must not be null");
        List<EventListenerDefinition> definitions = new ArrayList<>();

        for (Method method : beanClass.getDeclaredMethods()) {
            if (!method.isAnnotationPresent(EventListener.class)) {
                continue;
            }

            if (Modifier.isStatic(method.getModifiers())) {
                throw invalid(method, "must be an instance method");
            }
            if (method.getParameterCount() != 1) {
                throw invalid(method, "must have exactly one parameter");
            }
            if (method.getReturnType() != void.class) {
                throw invalid(method, "must return void");
            }

            Class<?> eventType = method.getParameterTypes()[0];
            if (eventType.isPrimitive()) {
                throw invalid(method, "parameter must be an event object, not a primitive");
            }
            if (!method.trySetAccessible()) {
                throw invalid(method, "is not accessible to the event dispatcher");
            }

            definitions.add(new EventListenerDefinition(beanClass, method, eventType));
        }

        return List.copyOf(definitions);
    }

    private IllegalArgumentException invalid(Method method, String reason) {
        return new IllegalArgumentException(
                "Invalid @EventListener method " + method + ": " + reason
        );
    }
}
