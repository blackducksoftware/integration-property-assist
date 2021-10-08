/*
 * configuration
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.propertyassist.config.value;

import java.util.Optional;

import com.synopsys.integration.propertyassist.config.resolution.PropertyResolutionInfo;
import com.synopsys.integration.propertyassist.parse.ValueParseException;

public class NoValuePropertyValue<T> extends PropertyValue<T> {
    @Override
    public Optional<T> getValue() {
        return Optional.empty();
    }

    @Override
    public Optional<PropertyResolutionInfo> getResolutionInfo() {
        return Optional.empty();
    }

    @Override
    public Optional<ValueParseException> getException() {
        return Optional.empty();
    }
}
