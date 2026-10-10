package com.minispring.lifecycle;

/**
 * Implemented by beans that need to know their registered name.
 */
public interface BeanNameAware {

    void setBeanName(String beanName);
}