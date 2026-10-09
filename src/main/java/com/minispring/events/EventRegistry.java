package com.minispring.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class EventRegistry {

    private final Map<Class<?>, List<EventListenerDefinition>> listeners =
            new HashMap<>();

    public void register(EventListenerDefinition definition) {
        Objects.requireNonNull(definition, "Definition must not be null");
        listeners.computeIfAbsent(
                definition.getEventType(),
                eventType -> new ArrayList<>()
        ).add(definition);
    }

    public List<EventListenerDefinition> getListeners(Class<?> eventType) {
        Objects.requireNonNull(eventType, "Event type must not be null");

        return List.copyOf(
                listeners.getOrDefault(eventType, List.of())
        );
    }
}