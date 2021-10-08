/*
 * configuration
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.propertyassist.property.types.path;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class TildeInPathResolver implements PathResolver {
    private final String systemUserHome;
    private BiConsumer<String, String> resolvedPathChanged = (from, to) -> {};

    public TildeInPathResolver(final String systemUserHome) {
        this.systemUserHome = systemUserHome;
    }

    /**
     * Resolves a '~' character at the start of [filePath]. In linux/mac environments, this
     * is shorthand for the user's home directory. If we encounter a property that
     * is formed this way, we can resolve it.
     */
    @Override
    public Path resolvePath(final String filePath) {
        final String resolved;
        if (filePath.startsWith("~/")) {
            resolved = systemUserHome + filePath.substring(1);
        } else {
            resolved = filePath;
        }

        if (!resolved.equals(filePath)) {
            resolvedPathChanged.accept(filePath, resolved);
        }

        return Paths.get(resolved);
    }
}

