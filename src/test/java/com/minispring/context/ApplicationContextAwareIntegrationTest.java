package com.minispring.context;

import com.minispring.context.awaretest.ContextAwareComponent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class ApplicationContextAwareIntegrationTest {

    @Test
    void injectsTheOwningApplicationContext()
            throws Exception {

        try (ApplicationContext context =
                     new ApplicationContext(
                             "com.minispring.context.awaretest"
                     )) {

            ContextAwareComponent bean =
                    context.getBean(ContextAwareComponent.class);

            assertNotNull(bean.getApplicationContext());

            assertSame(
                    context,
                    bean.getApplicationContext()
            );

            assertSame(
                    context.getEnvironment(),
                    bean.getApplicationContext().getEnvironment()
            );
        }
    }
}