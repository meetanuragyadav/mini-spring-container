package com.minispring.demo;

import com.minispring.annotation.Component;
import com.minispring.annotation.Profile;

/** Demonstrates profile-based bean registration. */
@Component
@Profile("dev")
public class DevOnlyFeature {
}
