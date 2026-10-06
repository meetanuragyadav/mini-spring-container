package com.minispring.demo;

import com.minispring.annotation.Component;
import com.minispring.annotation.ConditionalOnProperty;

/** Demonstrates property-based conditional bean registration. */
@Component
@ConditionalOnProperty(name = "feature.enabled", havingValue = "true")
public class PropertyFeature {
}
