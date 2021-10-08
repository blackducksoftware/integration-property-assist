/*
 * configuration
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.propertyassist.property.types.enumsoft;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.synopsys.integration.propertyassist.parse.ListValueParser;
import com.synopsys.integration.propertyassist.property.base.ValuedListProperty;
import com.synopsys.integration.propertyassist.util.EnumPropertyUtils;
import com.synopsys.integration.propertyassist.util.PropertyUtils;

public class SoftEnumListProperty<E extends Enum<E>> extends ValuedListProperty<SoftEnumValue<E>> {
    @NotNull
    private final Class<E> enumClass;

    public SoftEnumListProperty(@NotNull final String key, List<SoftEnumValue<E>> defaultValue, @NotNull Class<E> enumClass) {
        super(key, new ListValueParser<>(new SoftEnumValueParser<>(enumClass)), defaultValue);
        this.enumClass = enumClass;
    }

    @Nullable
    @Override
    public String describeDefault() {
        return PropertyUtils.describeObjectList(getDefaultValue());
    }

    @Override
    public boolean isCaseSensitive() {
        return false;
    }

    @Nullable
    @Override
    public List<String> listExampleValues() {
        return EnumPropertyUtils.getEnumNames(enumClass);
    }

    @Override
    public boolean isOnlyExampleValues() {
        return false;
    }

    @Nullable
    @Override
    public String describeType() {
        return enumClass.getSimpleName() + " List";
    }
}