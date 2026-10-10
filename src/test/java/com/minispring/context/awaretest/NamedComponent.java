package com.minispring.context.awaretest;

import com.minispring.annotation.Component;
import com.minispring.lifecycle.BeanNameAware;

@Component
public class NamedComponent implements BeanNameAware {

    private String beanName;

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getBeanName() {
        return beanName;
    }
}