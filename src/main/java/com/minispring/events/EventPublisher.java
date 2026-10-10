package com.minispring.events;

import com.minispring.core.BeanResolver;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Synchronously dispatches an event to registered listener methods.
 *
 * <p>This first version matches the event's exact runtime class. Listener
 * beans are obtained through BeanResolver so normal container scopes and
 * lifecycle processing are used.</p>
 */
public final class EventPublisher {

    // Listener metadata is separate from listener object instances.
    private final EventRegistry registry;

    // Resolve listener beans through MiniSpring so their scope/lifecycle is respected.
    private final BeanResolver beanResolver;

    public EventPublisher(EventRegistry registry, BeanResolver beanResolver) {
        this.registry = Objects.requireNonNull(registry, "Registry must not be null");
        this.beanResolver = Objects.requireNonNull(beanResolver, "BeanResolver must not be null");
    }

    /**
     * Publish an event synchronously. If a listener fails, dispatch stops and
     * an EventDispatchException exposes the failing listener and original cause.
     */
    public void publishEvent(Object event) {
        Objects.requireNonNull(event, "Event must not be null");
        // This version intentionally uses exact runtime-class matching.
        var definitions = registry.getListeners(event.getClass());

        // Reuse one resolved instance per listener class for this dispatch.
        // A new map is created for each published event.
        Map<Class<?>, Object> beansForThisDispatch = new HashMap<>();

        for (EventListenerDefinition definition : definitions) {
            try {
                Object bean = beansForThisDispatch.get(definition.getBeanClass());
                if (bean == null) {
                    bean = beanResolver.resolve(definition.getBeanClass());
                    beansForThisDispatch.put(definition.getBeanClass(), bean);
                }

                Method method = definition.getMethod();
                if (!method.canAccess(bean) && !method.trySetAccessible()) {
                    throw new IllegalAccessException(
                            "Listener method is not accessible: " + method
                    );
                }

                method.invoke(bean, event);
            } catch (InvocationTargetException exception) {
                Throwable cause = exception.getCause() == null
                        ? exception
                        : exception.getCause();
                throw new EventDispatchException(
                        "Event listener failed: " + definition.getMethod(), cause
                );
            } catch (Exception exception) {
                throw new EventDispatchException(
                        "Could not dispatch event to listener: "
                                + definition.getMethod(), exception
                );
            }
        }
    }
}
