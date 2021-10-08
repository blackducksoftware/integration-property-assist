/*
 * configuration
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.propertyassist.property.types.enumextended;

import java.util.Optional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.apache.commons.lang3.Validate;

public class ExtendedEnumValue<E extends Enum<E>, B extends Enum<B>> {
    @Nullable
    private final E extendedValue;
    @Nullable
    private final B baseValue;

    private ExtendedEnumValue(@Nullable final E extendedValue, @Nullable final B baseValue) {
        if (baseValue == null) {
            Validate.notNull(extendedValue, "You must provide either a base value or an extended value.");
        } else if (extendedValue == null) {
            Validate.notNull(baseValue, "You must provide either a base value or an extended value.");
        }
        Validate.isTrue(baseValue == null || extendedValue == null, "One value must be null.");

        this.baseValue = baseValue;
        this.extendedValue = extendedValue;
    }

    @NotNull
    public static <E extends Enum<E>, B extends Enum<B>> ExtendedEnumValue<E, B> ofBaseValue(@NotNull final B baseValue) {
        return new ExtendedEnumValue<>(null, baseValue);
    }

    @NotNull
    public static <E extends Enum<E>, B extends Enum<B>> ExtendedEnumValue<E, B> ofExtendedValue(@NotNull final E extendedValue) {
        return new ExtendedEnumValue<>(extendedValue, null);
    }

    @NotNull
    public Optional<E> getExtendedValue() {
        return Optional.ofNullable(extendedValue);
    }

    @NotNull
    public Optional<B> getBaseValue() {
        return Optional.ofNullable(baseValue);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final ExtendedEnumValue<?, ?> that = (ExtendedEnumValue<?, ?>) o;

        if (getExtendedValue().isPresent() ? !getExtendedValue().equals(that.getExtendedValue()) : that.getExtendedValue().isPresent()) {
            return false;
        }
        return getBaseValue().isPresent() ? getBaseValue().equals(that.getBaseValue()) : !that.getBaseValue().isPresent();
    }

    @Override
    public int hashCode() {
        int result = getExtendedValue().isPresent() ? getExtendedValue().hashCode() : 0;
        result = 31 * result + (getBaseValue().isPresent() ? getBaseValue().hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        if (getBaseValue().isPresent()) {
            return getBaseValue().map(Enum::name).get();
        } else if (getExtendedValue().isPresent()) {
            return getExtendedValue().map(Enum::name).get();
        } else {
            throw new IllegalStateException("Extended enum values should be created with a default value.");
        }
    }
}
