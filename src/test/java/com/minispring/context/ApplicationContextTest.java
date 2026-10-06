package com.minispring.context;

import com.minispring.annotation.Component;
import com.minispring.annotation.ConditionalOnProperty;
import com.minispring.annotation.Profile;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationContextTest {

    @Test
    void profileControlsComponentRegistration() throws Exception {
        Environment environment = new Environment();
        environment.addProfile("test");

        try (ApplicationContext context =
                     new ApplicationContext("com.minispring.context", environment)) {
            assertNotNull(context.getBean(ProfiledComponent.class));
        }
    }

    @Test
    void propertyConditionControlsComponentRegistration() throws Exception {
        Environment environment = new Environment();
        environment.setProperty("feature.enabled", "true");

        try (ApplicationContext context =
                     new ApplicationContext("com.minispring.context", environment)) {
            assertNotNull(context.getBean(PropertyComponent.class));
        }
    }

    @Component
    @Profile("test")
    static class ProfiledComponent {
    }

    @Component
    @ConditionalOnProperty(
            name = "feature.enabled",
            havingValue = "true")
    static class PropertyComponent {
    }
}
