package com.minispring.demo;

import com.minispring.annotation.*;
import com.minispring.context.*;
import com.minispring.core.*;
import com.minispring.lifecycle.*;

/**
 * Demonstrates deferred prototype resolution through {@link Provider}.
 *
 * Injecting the provider keeps the lookup lazy, so a new prototype can be
 * requested each time {@link #getPrototype()} is called.
 */
@Component
public class ScopeConsumer {

    private final Provider<PrototypeTest> provider;

    @Inject
    public ScopeConsumer(
            Provider<PrototypeTest> provider) {

        this.provider = provider;
    }

    public PrototypeTest getPrototype()
            throws Exception {

        return provider.get();
    }
}
