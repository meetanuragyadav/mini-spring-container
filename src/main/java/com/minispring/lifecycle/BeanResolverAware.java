package com.minispring.lifecycle;

import com.minispring.core.BeanResolver;

/**
 * Implemented by beans that need access to the bean resolver.
 */
public interface BeanResolverAware {

    void setBeanResolver(BeanResolver beanResolver);
}