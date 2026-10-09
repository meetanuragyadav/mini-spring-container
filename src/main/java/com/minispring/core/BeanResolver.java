
package com.minispring.core;

/**
 * Provides access to beans without exposing the Container directly.
 */
@FunctionalInterface
public interface BeanResolver {

    Object resolve(Class<?> beanClass) throws Exception;
}
