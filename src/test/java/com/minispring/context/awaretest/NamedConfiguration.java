package com.minispring.context.awaretest;

import com.minispring.annotation.Bean;
import com.minispring.annotation.Configuration;

@Configuration
public class NamedConfiguration {

    @Bean
    public NamedProduct namedProduct() {
        return new NamedProduct();
    }
}