/*
 * configuration
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.propertyassist.source;

import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// IMPORTANT
// A property source is responsible for responding with keys in the normalized form "example.key"
// It must respond to well formed keys in the normalized form "example.key"
// Use KeyUtils if you have keys from unknown sources to ensure consistent key format.
public interface PropertySource {
    @NotNull
    Boolean hasKey(String key);

    @NotNull
    Set<String> getKeys();

    @Nullable
    String getValue(String key);

    @Nullable
    String getOrigin(String key);

    @NotNull
    String getName();
}
