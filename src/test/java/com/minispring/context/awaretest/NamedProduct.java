package com.minispring.context.awaretest;

import com.minispring.lifecycle.BeanNameAware;

public class NamedProduct implements BeanNameAware {

    private String beanName;

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getBeanName() {
        return beanName;
    }
}