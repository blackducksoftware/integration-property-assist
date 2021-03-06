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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.synopsys.integration.propertyassist.parse.ValueParseException;

public class LongValueParserTests {
    private final LongValueParser parser = new LongValueParser();

    @ParameterizedTest
    @ValueSource(strings = { "unknown", "Nan", "", " 1", "1L", "9223372036854775808" })
    public void parseUnknownThrows(final String value) {
        Assertions.assertThrows(ValueParseException.class, () -> parser.parse(value));
    }

    @Test
    public void parseLong() throws ValueParseException {
        Assertions.assertEquals(new Long(-1), parser.parse("-1"));
        Assertions.assertEquals(new Long(1), parser.parse("1"));
        Assertions.assertEquals(new Long(Long.MAX_VALUE), parser.parse("9223372036854775807"));
        Assertions.assertEquals(new Long(Long.MIN_VALUE), parser.parse("-9223372036854775808"));
    }
}