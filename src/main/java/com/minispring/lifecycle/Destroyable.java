package com.minispring.lifecycle;

import com.minispring.core.Container;

/**
 * Optional shutdown callback for managed singleton beans.
 */
public interface Destroyable {

    void destroy();
}
