package com.minispring.core;

import com.minispring.lifecycle.BeanResolverAware;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BeanResolverAwareIntegrationTest {

    static class TargetBean {
    }

    static class ClientBean implements BeanResolverAware {

        private BeanResolver beanResolver;

        @Override
        public void setBeanResolver(BeanResolver beanResolver) {
            this.beanResolver = beanResolver;
        }
    }

    @Test
    void providesResolverBeforeBeanIsUsed() throws Exception {
        Container container = new Container();

        container.register(new BeanDefinition(
                TargetBean.class,
                Scope.SINGLETON,
                null,
                false
        ));

        container.register(new BeanDefinition(
                ClientBean.class,
                Scope.SINGLETON,
                null,
                false
        ));

        ClientBean client =
                (ClientBean) container.resolve(ClientBean.class);

        assertNotNull(client.beanResolver);

        Object target =
                client.beanResolver.resolve(TargetBean.class);

        assertNotNull(target);
        assertInstanceOf(TargetBean.class, target);
    }
}