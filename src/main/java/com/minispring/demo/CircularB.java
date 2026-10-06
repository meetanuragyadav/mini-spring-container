package com.minispring.demo;

import com.minispring.annotation.*;
import com.minispring.context.*;
import com.minispring.core.*;
import com.minispring.lifecycle.*;

/**
 * Second node in the circular-dependency demonstration.
 *
 * The Provider delays resolution of CircularA until {@link #getA()} is called.
 */
@Component
public class CircularB {

    private final Provider<CircularA> provider;

    @Inject
    public CircularB(
            Provider<CircularA> provider) {

        this.provider = provider;
    }

    public CircularA getA()
            throws Exception {

        return provider.get();
    }
}
