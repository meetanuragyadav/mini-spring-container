package com.minispring.lifecycle;

import com.minispring.core.Container;

/**
 * Hook around bean initialization.
 *
 * A processor may inspect a bean or return a different object. That ability
 * is important for later concepts such as proxies and AOP.
 */
public interface BeanPostProcessor {

    Object beforeInitialization(Object bean);

    Object afterInitialization(Object bean);
}
