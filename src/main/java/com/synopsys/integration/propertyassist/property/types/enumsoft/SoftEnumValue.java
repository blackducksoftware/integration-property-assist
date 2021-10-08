/*
 * configuration
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.propertyassist.property.types.enumsoft;

import java.util.Objects;
import java.util.Optional;

import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// An enum that can be the given ENUM or can be STRING
// Useful for properties that might want to be extended by the user such as Black Duck settings where we may know some of the values but don't care if we do not.
public class SoftEnumValue<T extends Enum<T>> {
    @Nullable
    private final String softValue;
    @Nullable
    private final T enumValue;

    private SoftEnumValue(@Nullable final String softValue, @Nullable final T enumValue) {
        if (softValue == null) {
            Validate.notNull(enumValue, "You must provide either a enum value or a soft value.");
        } else if (enumValue == null) {
            Validate.notNull(softValue, "You must provide either a enum value or a soft value.");
        }
        Validate.isTrue(enumValue == null || softValue == null, "One value must be null.");

        this.softValue = softValue;
        this.enumValue = enumValue;
    }

    @NotNull
    public static <E extends Enum<E>> SoftEnumValue<E> ofSoftValue(@NotNull final String baseValue) {
        return new SoftEnumValue<>(baseValue, null);
    }

    @NotNull
    public static <E extends Enum<E>> SoftEnumValue<E> ofEnumValue(@NotNull final E enumValue) {
        return new SoftEnumValue<>(null, enumValue);
    }

    public Optional<String> getSoftValue() {
        return Optional.ofNullable(softValue);
    }

    public Optional<T> getEnumValue() {
        return Optional.ofNullable(enumValue);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final SoftEnumValue<?> that = (SoftEnumValue<?>) o;
        return Objects.equals(softValue, that.softValue) &&
                   Objects.equals(enumValue, that.enumValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(softValue, enumValue);
    }

    @Override
    public String toString() {
        if (enumValue != null) {
            return enumValue.toString();
        } else {
            return softValue;
        }
    }
}


