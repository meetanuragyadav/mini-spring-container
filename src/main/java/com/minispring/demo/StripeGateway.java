package com.minispring.demo;

import com.minispring.annotation.*;
import com.minispring.context.*;
import com.minispring.core.*;
import com.minispring.lifecycle.*;

/**
 * One PaymentGateway implementation selected by default with {@link Primary}.
 */
@Primary
@Qualifier("stripe")
@Component
class StripeGateway implements PaymentGateway {

    @Override
    public void pay() {
        System.out.println("Stripe payment");
    }
}
