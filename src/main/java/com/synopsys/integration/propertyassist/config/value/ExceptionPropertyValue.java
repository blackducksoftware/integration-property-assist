/*
 * configuration
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.propertyassist.config.value;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.apache.commons.lang3.Validate;

import com.synopsys.integration.propertyassist.config.resolution.PropertyResolutionInfo;
import com.synopsys.integration.propertyassist.parse.ValueParseException;

public class ExceptionPropertyValue<T> extends ResolvedPropertyValue<T> {
    @NotNull
    private final ValueParseException exception;

    public ExceptionPropertyValue(@NotNull final ValueParseException exception, @NotNull final PropertyResolutionInfo propertyResolutionInfo) {
        super(propertyResolutionInfo);
        Validate.notNull(exception, "Exception cannot be null.");
        Validate.notNull(propertyResolutionInfo, "PropertyResolutionInfo cannot be null.");
        this.exception = exception;
    }

    @Override
    public Optional<T> getValue() {
        return Optional.empty();
    }

    @Override
    public Optional<ValueParseException> getException() {
        return Optional.of(exception);
    }
}
