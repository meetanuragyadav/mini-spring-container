package com.minispring.core;

import com.minispring.annotation.*;
import com.minispring.lifecycle.*;

/**
 * Controls how the container manages bean instances.
 */
public enum Scope {

    /** One managed instance is cached and reused. */
    SINGLETON,

    /** A new instance is created for each container resolution. */
    PROTOTYPE
}
