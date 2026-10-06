package com.minispring.condition;

import com.minispring.context.Environment;

/**
 * Condition based on a configuration property.
 *
 * <p>The condition matches when the property's actual value
 * equals the expected value.</p>
 */
public class PropertyCondition implements Condition {

    private final String propertyName;
    private final String expectedValue;

    public PropertyCondition(
            String propertyName,
            String expectedValue) {

        if (propertyName == null || propertyName.isBlank()) {
            throw new IllegalArgumentException(
                    "Property name cannot be empty."
            );
        }

        this.propertyName = propertyName;
        this.expectedValue = expectedValue;
    }

    @Override
    public boolean matches(Environment environment) {

        String actualValue =
                environment.getProperty(propertyName);

        return expectedValue.equals(actualValue);
    }
}
