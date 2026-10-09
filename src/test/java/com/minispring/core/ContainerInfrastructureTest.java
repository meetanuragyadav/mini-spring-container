package com.minispring.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;

class ContainerInfrastructureTest {

    static class ExampleBean {
    }

    @Test
    void containerResolvesItselfThroughBeanResolverContract() throws Exception {
        Container container = new Container();
        assertSame(container, container.resolve(BeanResolver.class));
    }

    @Test
    void singletonBeanIsReused() throws Exception {
        Container container = new Container();
        container.register(new BeanDefinition(ExampleBean.class, Scope.SINGLETON, null, false));

        Object first = container.resolve(ExampleBean.class);
        Object second = container.resolve(ExampleBean.class);

        assertSame(first, second);
    }
}
