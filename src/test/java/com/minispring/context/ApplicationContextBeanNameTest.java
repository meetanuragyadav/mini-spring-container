package com.minispring.context;

import com.minispring.context.awaretest.NamedComponent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApplicationContextBeanNameTest {

    @Test
    void assignsDefaultNameToDiscoveredComponent()
            throws Exception {

        try (ApplicationContext context =
                     new ApplicationContext(
                             "com.minispring.context.awaretest"
                     )) {

            NamedComponent bean =
                    context.getBean(NamedComponent.class);

            assertEquals(
                    "namedComponent",
                    bean.getBeanName()
            );
        }
    }
}