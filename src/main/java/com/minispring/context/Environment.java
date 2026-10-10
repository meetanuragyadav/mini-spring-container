package com.minispring.context;

import com.minispring.annotation.*;
import com.minispring.condition.*;
import com.minispring.core.*;
import com.minispring.lifecycle.*;
import com.minispring.scanner.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Represents the runtime environment of the application.
 *
 * <p>The environment contains configuration information that can
 * influence how the ApplicationContext is constructed.</p>
 *
 * <p>Currently it supports:</p>
 *
 * <ul>
 *     <li>Active profiles</li>
 *     <li>String-based configuration properties</li>
 * </ul>
 *
 * <p>The Environment does not decide whether a bean should exist.
 * Conditions use the Environment to make that decision.</p>
 */
public class Environment {

    // Profiles enable groups of environment-specific components.
    private final Set<String> activeProfiles = new HashSet<>();

    // Simple string configuration values consumed by conditional rules.
    private final Map<String, String> properties = new HashMap<>();

    // =============================================================
    // PROFILES
    // =============================================================

    /**
     * Activates a profile.
     *
     * @param profile profile name
     */
    public void addProfile(String profile) {

        if (profile == null || profile.isBlank()) {
            throw new IllegalArgumentException(
                    "Profile name cannot be empty."
            );
        }

        activeProfiles.add(profile);
    }

    /**
     * Checks whether a profile is active.
     *
     * @param profile profile name
     * @return true if the profile is active
     */
    public boolean isProfileActive(String profile) {
        return activeProfiles.contains(profile);
    }

    /**
     * Returns the currently active profiles.
     *
     * @return immutable set of active profiles
     */
    public Set<String> getActiveProfiles() {
        return Collections.unmodifiableSet(activeProfiles);
    }

    // =============================================================
    // PROPERTIES
    // =============================================================

    /**
     * Sets a configuration property.
     *
     * @param key property name
     * @param value property value
     */
    public void setProperty(String key, String value) {

        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException(
                    "Property key cannot be empty."
            );
        }

        properties.put(key, value);
    }

    /**
     * Returns a configuration property.
     *
     * @param key property name
     * @return property value, or null if absent
     */
    public String getProperty(String key) {
        return properties.get(key);
    }

    /**
     * Returns a property or a supplied default value.
     *
     * @param key property name
     * @param defaultValue value to return when the property is absent
     * @return configured value or default value
     */
    public String getProperty(
            String key,
            String defaultValue) {

        return properties.getOrDefault(
                key,
                defaultValue
        );
    }
}
