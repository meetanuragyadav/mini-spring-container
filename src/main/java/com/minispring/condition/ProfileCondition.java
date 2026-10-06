package com.minispring.condition;

import com.minispring.context.Environment;

/**
 * Condition based on active application profiles.
 *
 * <p>The condition matches when at least one of the required
 * profiles is active.</p>
 */
public class ProfileCondition implements Condition {

    private final String[] profiles;

    public ProfileCondition(String[] profiles) {

        if (profiles == null || profiles.length == 0) {
            throw new IllegalArgumentException(
                    "At least one profile is required."
            );
        }

        this.profiles = profiles.clone();
    }

    @Override
    public boolean matches(Environment environment) {

        for (String profile : profiles) {

            if (environment.isProfileActive(profile)) {
                return true;
            }
        }

        return false;
    }
}
