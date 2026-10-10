package com.minispring.context;

import com.minispring.context.awaretest.NamedProduct;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ApplicationContextFactoryBeanNameTest {

    @Test
    void assignsFactoryMethodNameToCreatedBean()
            throws Exception {

        try (ApplicationContext context =
                     new ApplicationContext(
                             "com.minispring.context.awaretest"
                     )) {

            NamedProduct product =
                    context.getBean(NamedProduct.class);

            assertEquals(
                    "namedProduct",
                    product.getBeanName()
            );
        }
    }
}