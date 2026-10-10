package com.minispring.lifecycle;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BeanNameAwareTest {

    static class TestBean implements BeanNameAware {

        private String beanName;

        @Override
        public void setBeanName(String beanName) {
            this.beanName = beanName;
        }
    }

    @Test
    void shouldAcceptBeanName() {
        TestBean bean = new TestBean();

        bean.setBeanName("testBean");

        assertEquals("testBean", bean.beanName);
    }
}