/*
 * configuration
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.propertyassist.config.resolution;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.apache.commons.lang3.Validate;

public class SourcePropertyResolution extends PropertyResolution {
    @NotNull
    private PropertyResolutionInfo propertyResolutionInfo;

    public SourcePropertyResolution(final @NotNull PropertyResolutionInfo propertyResolutionInfo) {
        Validate.notNull(propertyResolutionInfo, "Cannot create a source property resolution without supplying property info, use NoPropertyResolution if this was intentional.");
        this.propertyResolutionInfo = propertyResolutionInfo;
    }

    @Override
    public Optional<PropertyResolutionInfo> getResolutionInfo() {
        return Optional.of(propertyResolutionInfo);
    }
}
