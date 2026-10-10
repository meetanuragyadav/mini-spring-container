package com.minispring.lifecycle;

import com.minispring.core.BeanResolver;

/**
 * Implemented by beans that need access to the bean resolver.
 *
 * This is a MiniSpring-specific teaching interface, not a standard Spring
 * Framework Aware interface.
 */
public interface BeanResolverAware {

    void setBeanResolver(BeanResolver beanResolver);
}