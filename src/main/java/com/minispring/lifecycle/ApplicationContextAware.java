package com.minispring.lifecycle;

import com.minispring.context.ApplicationContext;

/**
 * MiniSpring callback for beans that genuinely need their owning context.
 *
 * Prefer a narrower dependency (such as Environment or an event-publishing
 * abstraction) when that is all the bean requires.
 */
public interface ApplicationContextAware {

    void setApplicationContext(
            ApplicationContext applicationContext
    );
}