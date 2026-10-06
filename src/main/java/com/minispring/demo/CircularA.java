package com.minispring.demo;

import com.minispring.annotation.*;
import com.minispring.context.*;
import com.minispring.core.*;
import com.minispring.lifecycle.*;

/**
 * First node in the circular-dependency demonstration.
 *
 * CircularA directly depends on CircularB. CircularB uses a Provider for A,
 * allowing us to demonstrate deferred resolution instead of an immediate
 * construction-time cycle.
 */
@Component
public class CircularA {

    private final CircularB b;

    @Inject
    public CircularA(CircularB b) {
        this.b = b;
    }
}
