package com.minispring.demo;

import com.minispring.annotation.*;
import com.minispring.context.*;
import com.minispring.core.*;
import com.minispring.lifecycle.*;

/**
 * Prototype-scoped component used to demonstrate that each resolution creates
 * a new instance.
 */
@Component
@BeanScope(Scope.PROTOTYPE)
public class PrototypeTest {

    public PrototypeTest() {
        System.out.println(
                "PrototypeTest constructor"
        );
    }
}
