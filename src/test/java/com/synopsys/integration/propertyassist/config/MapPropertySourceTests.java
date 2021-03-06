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
package com.synopsys.integration.propertyassist.config;

import java.util.Collections;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.propertyassist.source.MapPropertySource;
import com.synopsys.integration.propertyassist.source.PropertySource;

class MapPropertySourceTests {
    @Test
    public void testNormalizesKeys() {
        final PropertySource source = new MapPropertySource("test", Collections.singletonMap("CAPITAL_UNDERSCORE", "value"));
        final Set<String> keys = source.getKeys();
        Assertions.assertEquals(Collections.singleton("capital.underscore"), keys);
    }

    @Test
    public void returnsKey() {
        final PropertySource source = new MapPropertySource("test", Collections.singletonMap("property.key", "value"));
        Assertions.assertEquals("value", source.getValue("property.key"));
        Assertions.assertEquals("test", source.getOrigin("property.key"));
        Assertions.assertEquals("test", source.getName());
    }
}