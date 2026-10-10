package com.minispring.lifecycle;

/**
 * Implemented by beans that need to know their registered name.
 *
 * MiniSpring currently uses class-keyed registration, so the name is supplied
 * as metadata and is not yet a general name-based lookup key.
 */
public interface BeanNameAware {

    void setBeanName(String beanName);
}