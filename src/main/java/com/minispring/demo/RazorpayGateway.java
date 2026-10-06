package com.minispring.demo;

import com.minispring.annotation.*;
import com.minispring.context.*;
import com.minispring.core.*;
import com.minispring.lifecycle.*;

/**
 * One PaymentGateway implementation selected explicitly with a qualifier.
 */
@Qualifier("razorpay")
@Component
class RazorpayGateway implements PaymentGateway {

    @Override
    public void pay() {
        System.out.println("Razorpay payment");
    }
}
