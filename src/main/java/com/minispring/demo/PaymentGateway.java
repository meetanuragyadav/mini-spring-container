package com.minispring.demo;

import com.minispring.annotation.*;
import com.minispring.context.*;
import com.minispring.core.*;
import com.minispring.lifecycle.*;

/**
 * Abstraction used to demonstrate interface-based dependency injection.
 */
public interface PaymentGateway {
    void pay();
}
