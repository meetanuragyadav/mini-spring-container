package com.minispring.demo;

import com.minispring.annotation.*;
import com.minispring.context.*;
import com.minispring.core.*;
import com.minispring.lifecycle.*;

@Component
public class LoggingPostProcessor
        implements BeanPostProcessor {

    @Override
    public Object beforeInitialization(Object bean) {

        System.out.println(
                "Before initialization: "
                        + bean.getClass().getSimpleName()
        );

        return bean;
    }

    @Override
    public Object afterInitialization(Object bean) {

        System.out.println(
                "After initialization: "
                        + bean.getClass().getSimpleName()
        );

        return bean;
    }
}
