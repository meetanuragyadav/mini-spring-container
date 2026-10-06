package com.minispring.demo;

import com.minispring.annotation.*;
import com.minispring.context.*;
import com.minispring.core.*;
import com.minispring.lifecycle.*;
@Component
public class CircularC {

    private final CircularA a;

    @Inject
    public CircularC(CircularA a) {
        this.a = a;
    }
}
