/*
 * configuration
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.propertyassist.property.types.enumfilterable;

import java.util.Objects;
import java.util.Optional;

public class FilterableEnumValue<E extends Enum<E>> {
    private boolean all;
    private boolean none;
    private E value;

    private FilterableEnumValue(final boolean all, final boolean none, final E value) {
        this.all = all;
        this.none = none;
        this.value = value;
    }

    public boolean isAll() {
        return all;
    }

    public boolean isNone() {
        return none;
    }

    public Optional<E> getValue() {
        return Optional.ofNullable(value);
    }

    public static <E extends Enum<E>> FilterableEnumValue<E> allValue() {
        return new FilterableEnumValue<E>(true, false, null);
    }

    public static <E extends Enum<E>> FilterableEnumValue<E> noneValue() {
        return new FilterableEnumValue<E>(false, true, null);
    }

    public static <E extends Enum<E>> FilterableEnumValue<E> value(E value) {
        return new FilterableEnumValue<E>(false, false, value);
    }

    @Override
    public String toString() {
        if (isAll()) {
            return "ALL";
        } else if (isNone()) {
            return "NONE";
        } else {
            return value.toString();
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final FilterableEnumValue<?> that = (FilterableEnumValue<?>) o;
        return all == that.all &&
                   none == that.none &&
                   Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(all, none, value);
    }
}