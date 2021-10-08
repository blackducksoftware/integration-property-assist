/*
 * configuration
 *
 * Copyright (c) 2021 Synopsys, Inc.
 *
 * Use subject to the terms and conditions of the Synopsys End User Software License and Maintenance Agreement. All rights reserved worldwide.
 */
package com.synopsys.integration.propertyassist.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.apache.commons.lang3.Validate;

import com.synopsys.integration.propertyassist.config.resolution.NoPropertyResolution;
import com.synopsys.integration.propertyassist.config.resolution.PropertyResolution;
import com.synopsys.integration.propertyassist.config.resolution.PropertyResolutionInfo;
import com.synopsys.integration.propertyassist.config.resolution.SourcePropertyResolution;
import com.synopsys.integration.propertyassist.config.value.ExceptionPropertyValue;
import com.synopsys.integration.propertyassist.config.value.NoValuePropertyValue;
import com.synopsys.integration.propertyassist.config.value.PropertyValue;
import com.synopsys.integration.propertyassist.config.value.ValuedPropertyValue;
import com.synopsys.integration.propertyassist.parse.ValueParseException;
import com.synopsys.integration.propertyassist.property.Property;
import com.synopsys.integration.propertyassist.property.base.NullableProperty;
import com.synopsys.integration.propertyassist.property.base.PassthroughProperty;
import com.synopsys.integration.propertyassist.property.base.TypedProperty;
import com.synopsys.integration.propertyassist.property.base.ValuedProperty;
import com.synopsys.integration.propertyassist.source.PropertySource;

public class PropertyConfiguration {
    private final Map<String, PropertyResolution> resolutionCache = new HashMap<>();
    private final Map<String, PropertyValue<?>> valueCache = new HashMap<>();
    private final List<PropertySource> orderedPropertySources;

    public PropertyConfiguration(@NotNull final List<PropertySource> orderedPropertySources) {
        this.orderedPropertySources = orderedPropertySources;
    }

    //region
    @NotNull
    public <T> Optional<T> getValueOrEmpty(@NotNull final NullableProperty<T> property) {
        assertPropertyNotNull(property);
        try {
            return getValue(property);
        } catch (final InvalidPropertyException e) {
            return Optional.empty();
        }
    }

    @NotNull
    public <T> T getValueOrDefault(@NotNull final ValuedProperty<T> property) {
        assertPropertyNotNull(property);
        try {
            return getValue(property);
        } catch (final InvalidPropertyException e) {
            return property.getDefaultValue();
        }
    }

    @NotNull
    public <T> Optional<T> getValue(@NotNull final NullableProperty<T> property) throws InvalidPropertyException {
        assertPropertyNotNull(property);

        final PropertyValue<T> value = valueFromCache(property);
        final Optional<ValueParseException> parseException = value.getException();
        final Optional<PropertyResolutionInfo> propertyResolutionInfo = value.getResolutionInfo();

        if (value.getValue().isPresent()) {
            return value.getValue();
        } else if (parseException.isPresent() && propertyResolutionInfo.isPresent()) {
            throw new InvalidPropertyException(property.getKey(), propertyResolutionInfo.get().getSource(), parseException.get());
        } else {
            return Optional.empty();
        }
    }

    @NotNull
    public <T> T getValue(@NotNull final ValuedProperty<T> property) throws InvalidPropertyException {
        assertPropertyNotNull(property);
        final PropertyValue<T> propertyValue = valueFromCache(property);

        final Optional<T> value = propertyValue.getValue();
        final Optional<ValueParseException> parseException = propertyValue.getException();
        final Optional<PropertyResolutionInfo> propertyResolutionInfo = propertyValue.getResolutionInfo();

        if (value.isPresent()) {
            return value.get();
        } else if (parseException.isPresent() && propertyResolutionInfo.isPresent()) {
            throw new InvalidPropertyException(property.getKey(), propertyResolutionInfo.get().getSource(), parseException.get());
        } else {
            return property.getDefaultValue();
        }
    }

    public boolean wasKeyProvided(@NotNull final String key) {
        Validate.notNull(key, "Must provide a property.");
        return resolveFromCache(key).getResolutionInfo().isPresent();
    }

    public <T> boolean wasPropertyProvided(@NotNull final TypedProperty<T> property) {
        assertPropertyNotNull(property);
        return wasKeyProvided(property.getKey());
    }

    public Optional<String> getPropertySource(@NotNull final Property property) {
        assertPropertyNotNull(property);
        return resolveFromCache(property.getKey()).getResolutionInfo().map(PropertyResolutionInfo::getSource);
    }

    public Optional<String> getPropertySource(@NotNull final String key) {
        Validate.notNull(key, "You must provide a key");
        return resolveFromCache(key).getResolutionInfo().map(PropertyResolutionInfo::getSource);
    }

    public Optional<String> getPropertyOrigin(@NotNull final Property property) {
        assertPropertyNotNull(property);
        return resolveFromCache(property.getKey()).getResolutionInfo().map(PropertyResolutionInfo::getOrigin);
    }

    @NotNull
    public Set<String> getKeys() {
        return orderedPropertySources.stream()
                   .map(PropertySource::getKeys)
                   .flatMap(Set::stream)
                   .collect(Collectors.toSet());
    }

    public <T> Optional<ValueParseException> getPropertyException(@NotNull final TypedProperty<T> property) {
        assertPropertyNotNull(property);
        return valueFromCache(property).getException();
    }

    //endregion

    //region Advanced Usage
    public Optional<String> getRaw(@NotNull final Property property) {
        assertPropertyNotNull(property, "Must supply a property get raw values.");
        return resolveKey(property.getKey());
    }

    private Optional<String> resolveKey(String key) {
        final PropertyResolution propertyResolution = resolveFromCache(key);
        return propertyResolution.getResolutionInfo().map(PropertyResolutionInfo::getRaw);
    }

    @NotNull
    public Map<String, String> getRaw() {
        return getRaw((String key) -> true);
    }

    @NotNull
    public Optional<String> getRaw(@NotNull final String key) {
        Validate.notNull(key, "Must supply a key to get a raw value");
        return resolveFromCache(key).getResolutionInfo().map(PropertyResolutionInfo::getRaw);
    }

    @NotNull
    public Map<String, String> getRaw(@NotNull final Set<String> keys) {
        Validate.notNull(keys, "Must supply a set of keys to get raw values");
        return getRaw(keys::contains);
    }

    @NotNull
    public Map<String, String> getRawValueMap(@NotNull final Set<Property> properties) {
        return getMaskedRawValueMap(properties, key -> false);
    }

    @NotNull
    public Map<String, String> getMaskedRawValueMap(@NotNull final Set<Property> properties, Predicate<String> shouldMask) {
        Map<String, String> rawMap = new HashMap<>();
        for (Property property : properties) {
            String rawKey = property.getKey();
            getRaw(property).ifPresent(rawValue -> rawMap.put(rawKey, maskValue(rawKey, rawValue, shouldMask)));
        }
        return rawMap;
    }

    public String maskValue(String rawKey, String rawValue, Predicate<String> shouldMask) {
        String maskedValue = rawValue;
        if (shouldMask.test(rawKey)) {
            maskedValue = StringUtils.repeat('*', maskedValue.length());
        }
        return maskedValue;
    }

    @NotNull
    public Map<String, String> getRaw(@NotNull final Predicate<String> predicate) {
        Validate.notNull(predicate, "Must supply a predicate to get raw keys");

        final Set<String> keys = getKeys().stream()
                                     .filter(predicate)
                                     .filter(Objects::nonNull)
                                     .collect(Collectors.toSet());

        final Map<String, String> keyMap = new HashMap<>();
        keys.forEach(key -> {
            resolveKey(key).ifPresent(rawValue -> keyMap.put(key, rawValue));
        });
        return keyMap;
    }

    // Takes in a 'passthrough.key' and returns key map (whose keys have that value removed)
    // So value 'passthrough.key.example' is returned as 'example'
    @NotNull
    public Map<String, String> getRaw(@NotNull final PassthroughProperty property) {
        assertPropertyNotNull(property, "Must supply a passthrough to get raw keys");

        final Map<String, String> rawValues = getRaw((String key) -> key.startsWith(property.getKey()));
        final Map<String, String> trimmedKeys = new HashMap<>();
        for (final Map.Entry<String, String> entry : rawValues.entrySet()) {
            if (entry.getValue() != null) {
                trimmedKeys.put(property.trimKey(entry.getKey()), entry.getValue());
            }
        }
        return trimmedKeys;
    }
    //endregion Advanced Usage

    //region Implementation Details

    private void assertPropertyNotNull(final Property property) {
        assertPropertyNotNull(property, "Must provide a property.");
    }

    private void assertPropertyNotNull(final Property property, @NotNull final String message) {
        Validate.notNull(property, message);
    }

    private PropertyResolution resolveFromCache(@NotNull final String key) {
        Validate.notNull(key, "Cannot resolve a null key.");
        if (!resolutionCache.containsKey(key)) {
            resolutionCache.put(key, resolveFromPropertySources(key));
        }

        final PropertyResolution value = resolutionCache.get(key);
        Validate.notNull(value, "Could not resolve a value, something has gone wrong with properties!");
        return value;
    }

    private PropertyResolution resolveFromPropertySources(@NotNull final String key) {
        Validate.notNull(key, "Cannot resolve a null key.");
        for (final PropertySource propertySource : orderedPropertySources) {
            if (propertySource.hasKey(key)) {
                final String rawValue = propertySource.getValue(key);
                if (rawValue != null) {
                    final String name = propertySource.getName();
                    final String origin = propertySource.getOrigin(key);
                    final PropertyResolutionInfo propertyResolutionInfo = new PropertyResolutionInfo(name, origin, rawValue);
                    return new SourcePropertyResolution(propertyResolutionInfo);
                }
            }
        }
        return new NoPropertyResolution();
    }

    @NotNull
    private <T> PropertyValue<T> valueFromCache(@NotNull final TypedProperty<T> property) {
        if (!valueCache.containsKey(property.getKey())) {
            valueCache.put(property.getKey(), valueFromResolution(property));
        }

        @SuppressWarnings("unchecked") final PropertyValue<T> value = (PropertyValue<T>) valueCache.get(property.getKey());
        Validate.notNull(value, "Could not source a value, something has gone wrong with properties!");
        return value;
    }

    @NotNull
    private <T> PropertyValue<T> valueFromResolution(@NotNull final TypedProperty<T> property) {
        Validate.notNull(property, "Cannot resolve a null property.");
        final Optional<PropertyResolutionInfo> propertyResolution = resolveFromCache(property.getKey()).getResolutionInfo();
        return propertyResolution
                   .map(propertyResolutionInfo -> coerceValue(property, propertyResolutionInfo))
                   .orElseGet(NoValuePropertyValue::new);
    }

    @NotNull
    private <T> PropertyValue<T> coerceValue(@NotNull final TypedProperty<T> property, @NotNull final PropertyResolutionInfo propertyResolutionInfo) {
        Validate.notNull(property, "Cannot resolve a null property.");
        Validate.notNull(propertyResolutionInfo, "Cannot coerce a null property resolution.");
        try {
            final T value = property.getValueParser().parse(propertyResolutionInfo.getRaw());
            return new ValuedPropertyValue<>(value, propertyResolutionInfo);
        } catch (final ValueParseException e) {
            return new ExceptionPropertyValue<>(e, propertyResolutionInfo);
        }
    }
    //endregion
}


