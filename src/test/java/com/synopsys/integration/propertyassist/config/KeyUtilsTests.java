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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.synopsys.integration.propertyassist.util.KeyUtils;

public class KeyUtilsTests {
    @Test
    public void replacesUnderscores() {
        Assertions.assertEquals("key.dot.dot", KeyUtils.normalizeKey("key_dot_dot"));
    }

    @Test
    public void replacesCapitals() {
        Assertions.assertEquals("key.lower", KeyUtils.normalizeKey("KEY.LOWER"));
    }

    @Test
    public void normalizesEnvKey() {
        Assertions.assertEquals("env.key", KeyUtils.normalizeKey("ENV_KEY"));
    }
}