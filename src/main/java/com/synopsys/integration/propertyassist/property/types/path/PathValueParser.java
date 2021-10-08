/*
 * configuration
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.propertyassist.property.types.path;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import com.synopsys.integration.propertyassist.parse.ValueParseException;
import com.synopsys.integration.propertyassist.parse.ValueParser;

public class PathValueParser extends ValueParser<PathValue> {
    @NotNull
    @Override
    public PathValue parse(@NotNull final String value) throws ValueParseException {
        final String trimmedValue = value.trim();
        if (StringUtils.isNotBlank(trimmedValue)) {
            return new PathValue(trimmedValue);
        } else {
            throw new ValueParseException(trimmedValue, "Path", "A path must have at least one non-whitespace character!");
        }
    }
}
