package com.minispring.demo;

import com.minispring.annotation.*;
import com.minispring.context.*;
import com.minispring.core.*;
import com.minispring.lifecycle.*;

/**
 * Minimal component used by factory-method and post-processing examples.
 */
@Component
public class Logger {

    public Logger() {
    }

    public void log() {
        System.out.println("Logging...");
    }
}
