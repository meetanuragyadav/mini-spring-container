package com.minispring.core;

import com.minispring.lifecycle.BeanNameAware;
import com.minispring.lifecycle.BeanPostProcessor;
import com.minispring.lifecycle.Initializable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BeanNameAwareIntegrationTest {

    private static final List<String> CALLBACKS = new ArrayList<>();

    static class TestBean implements BeanNameAware, Initializable {

        private String beanName;

        // No constructor is needed.
        // MiniSpring can use the no-argument constructor.

        @Override
        public void setBeanName(String beanName) {
            this.beanName = beanName;
            CALLBACKS.add("aware");
        }

        @Override
        public void initialize() {
            CALLBACKS.add("initialize");
        }
    }

    @BeforeEach
    void resetCallbacks() {
        CALLBACKS.clear();
    }

    @Test
    void assignsBeanNameBeforeInitializationCallbacks()
            throws Exception {

        Container container = new Container();

        BeanDefinition definition = new BeanDefinition(
                TestBean.class,
                Scope.SINGLETON,
                null,
                false
        );

        definition.setBeanName("testBean");
        container.register(definition);

        container.addBeanPostProcessor(new BeanPostProcessor() {

            @Override
            public Object beforeInitialization(Object bean) {
                if (bean instanceof TestBean) {
                    CALLBACKS.add("before");
                }
                return bean;
            }

            @Override
            public Object afterInitialization(Object bean) {
                if (bean instanceof TestBean) {
                    CALLBACKS.add("after");
                }
                return bean;
            }
        });

        TestBean bean = (TestBean) container.resolve(TestBean.class);

        assertEquals("testBean", bean.beanName);

        assertEquals(
                List.of("aware", "before", "initialize", "after"),
                CALLBACKS
        );
    }
}
