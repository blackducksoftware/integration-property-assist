/**
 * configuration
 *
 * Copyright (c) 2020 Synopsys, Inc.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.synopsys.integration.propertyassist.property.types.longs;

import static com.synopsys.integration.propertyassist.util.ConfigTestUtils.configOf;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.propertyassist.config.InvalidPropertyException;
import com.synopsys.integration.propertyassist.config.PropertyConfiguration;
import com.synopsys.integration.propertyassist.property.PropertyTestHelpUtil;
import com.synopsys.integration.propertyassist.property.base.NullableProperty;
import com.synopsys.integration.propertyassist.property.base.ValuedListProperty;
import com.synopsys.integration.propertyassist.property.base.ValuedProperty;

// Simple glue sanity tests. Theoretically if Config is well tested and Parser is well tested, these will pass so they are not exhaustive.
public class LongPropertiesTests {
    @Test
    public void testNullable() throws InvalidPropertyException {
        final NullableProperty<Long> property = new NullableLongProperty("long.nullable");
        final PropertyConfiguration config = configOf(Pair.of("long.nullable", "2"));
        Assertions.assertEquals(Optional.of(2L), config.getValue(property));

        PropertyTestHelpUtil.assertAllHelpValid(property);
    }

    @Test
    public void testValued() throws InvalidPropertyException {
        final ValuedProperty<Long> property = new LongProperty("long.valued", 2L);
        final PropertyConfiguration config = configOf(Pair.of("long.valued", "5"));
        Assertions.assertEquals(new Long(5L), config.getValue(property));

        PropertyTestHelpUtil.assertAllHelpValid(property);
    }

    @Test
    public void testList() throws InvalidPropertyException {
        final ValuedListProperty<Long> property = new LongListProperty("long.list", Collections.emptyList());
        final PropertyConfiguration config = configOf(Pair.of("long.list", "2,3"));
        Assertions.assertEquals(Arrays.asList(2L, 3L), config.getValue(property));

        PropertyTestHelpUtil.assertAllListHelpValid(property);
    }
}