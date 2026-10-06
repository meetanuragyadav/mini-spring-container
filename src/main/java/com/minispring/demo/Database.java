package com.minispring.demo;

import com.minispring.annotation.*;
import com.minispring.context.*;
import com.minispring.core.*;
import com.minispring.lifecycle.*;

/**
 * Small lifecycle example representing an application resource.
 *
 * It prints messages during construction, initialization, and destruction so
 * the bean lifecycle can be observed while learning the container internals.
 */
@Component
public class Database
        implements Initializable, Destroyable {

    public Database() {
        System.out.println("Database constructor");
    }

    @Override
    public void initialize() {
        System.out.println("Database initialized");
    }

    @Override
    public void destroy() {
        System.out.println("Database destroyed");
    }
}
