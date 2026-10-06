package com.minispring.demo;

import com.minispring.annotation.*;
import com.minispring.context.*;
import com.minispring.core.*;
import com.minispring.lifecycle.*;

/**
 * Example configuration class used to demonstrate factory-method beans.
 *
 * The container discovers the {@link Bean} methods, stores their metadata,
 * and invokes them only when the corresponding bean is resolved.
 */
@Configuration
public class AppConfig {

    @Bean
    public PaymentGateway paymentGateway() {

        return new StripeGateway();
    }

    @Bean
    public FactoryArgumentTest factoryArgumentTest(
            Logger logger) {

        return new FactoryArgumentTest(logger);
    }
}
