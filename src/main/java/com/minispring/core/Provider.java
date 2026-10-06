package com.minispring.core;

import com.minispring.annotation.*;
import com.minispring.lifecycle.*;

/**
 * Deferred access to a bean.
 *
 * The provider itself is injected immediately, but the bean represented by
 * it is resolved only when get() is called. This is particularly useful when
 * a longer-lived bean needs repeated access to a shorter-lived bean such as a
 * prototype-scoped object.
 */
@FunctionalInterface
public interface Provider<T> {

    T get() throws Exception;
}
