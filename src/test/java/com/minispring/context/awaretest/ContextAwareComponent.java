package com.minispring.context.awaretest;

import com.minispring.annotation.Component;
import com.minispring.context.ApplicationContext;
import com.minispring.lifecycle.ApplicationContextAware;

@Component
public class ContextAwareComponent
        implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(
            ApplicationContext applicationContext) {

        this.applicationContext = applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}