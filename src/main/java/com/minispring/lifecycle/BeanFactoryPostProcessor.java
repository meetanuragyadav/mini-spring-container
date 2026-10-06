package com.minispring.lifecycle;

import com.minispring.core.Container;

/**
 * Hook for changing or inspecting container metadata before normal beans are
 * created. This operates on the {@link Container}, not on bean instances.
 */
public interface BeanFactoryPostProcessor {

    void postProcessBeanFactory(Container container);
}
